package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;

public class SaveLoading {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static CompletableFuture load(ServerConfig serverConfig, LoadContextSupplier loadContextSupplier, SaveApplierFactory saveApplierFactory, Executor prepareExecutor, Executor applyExecutor) {
      try {
         Pair pair = serverConfig.dataPacks.load();
         LifecycledResourceManager lifecycledResourceManager = (LifecycledResourceManager)pair.getSecond();
         CombinedDynamicRegistries combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
         List list = TagGroupLoader.startReload(lifecycledResourceManager, (DynamicRegistryManager)combinedDynamicRegistries.get(ServerDynamicRegistryType.STATIC));
         DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN);
         List list2 = TagGroupLoader.collectRegistries(immutable, list);
         DynamicRegistryManager.Immutable immutable2 = RegistryLoader.loadFromResource(lifecycledResourceManager, list2, RegistryLoader.DYNAMIC_REGISTRIES);
         List list3 = Stream.concat(list2.stream(), immutable2.stream()).toList();
         DynamicRegistryManager.Immutable immutable3 = RegistryLoader.loadFromResource(lifecycledResourceManager, list3, RegistryLoader.DIMENSION_REGISTRIES);
         DataConfiguration dataConfiguration = (DataConfiguration)pair.getFirst();
         RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(list3.stream());
         LoadContext loadContext = loadContextSupplier.get(new LoadContextSupplierContext(lifecycledResourceManager, dataConfiguration, wrapperLookup, immutable3));
         CombinedDynamicRegistries combinedDynamicRegistries2 = combinedDynamicRegistries.with(ServerDynamicRegistryType.WORLDGEN, (DynamicRegistryManager.Immutable[])(immutable2, loadContext.dimensionsRegistryManager));
         return DataPackContents.reload(lifecycledResourceManager, combinedDynamicRegistries2, list, dataConfiguration.enabledFeatures(), serverConfig.commandEnvironment(), serverConfig.functionPermissionLevel(), prepareExecutor, applyExecutor).whenComplete((dataPackContents, throwable) -> {
            if (throwable != null) {
               lifecycledResourceManager.close();
            }

         }).thenApplyAsync((dataPackContents) -> {
            dataPackContents.applyPendingTagLoads();
            return saveApplierFactory.create(lifecycledResourceManager, dataPackContents, combinedDynamicRegistries2, loadContext.extraData);
         }, applyExecutor);
      } catch (Exception var18) {
         return CompletableFuture.failedFuture(var18);
      }
   }

   public static record ServerConfig(DataPacks dataPacks, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
      final DataPacks dataPacks;

      public ServerConfig(DataPacks dataPacks, CommandManager.RegistrationEnvironment registrationEnvironment, int i) {
         this.dataPacks = dataPacks;
         this.commandEnvironment = registrationEnvironment;
         this.functionPermissionLevel = i;
      }

      public DataPacks dataPacks() {
         return this.dataPacks;
      }

      public CommandManager.RegistrationEnvironment commandEnvironment() {
         return this.commandEnvironment;
      }

      public int functionPermissionLevel() {
         return this.functionPermissionLevel;
      }
   }

   public static record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public DataPacks(ResourcePackManager resourcePackManager, DataConfiguration dataConfiguration, boolean bl, boolean bl2) {
         this.manager = resourcePackManager;
         this.initialDataConfig = dataConfiguration;
         this.safeMode = bl;
         this.initMode = bl2;
      }

      public Pair load() {
         DataConfiguration dataConfiguration = MinecraftServer.loadDataPacks(this.manager, this.initialDataConfig, this.initMode, this.safeMode);
         List list = this.manager.createResourcePacks();
         LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
         return Pair.of(dataConfiguration, lifecycledResourceManager);
      }

      public ResourcePackManager manager() {
         return this.manager;
      }

      public DataConfiguration initialDataConfig() {
         return this.initialDataConfig;
      }

      public boolean safeMode() {
         return this.safeMode;
      }

      public boolean initMode() {
         return this.initMode;
      }
   }

   public static record LoadContextSupplierContext(ResourceManager resourceManager, DataConfiguration dataConfiguration, RegistryWrapper.WrapperLookup worldGenRegistryManager, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
      public LoadContextSupplierContext(ResourceManager resourceManager, DataConfiguration dataConfiguration, RegistryWrapper.WrapperLookup wrapperLookup, DynamicRegistryManager.Immutable immutable) {
         this.resourceManager = resourceManager;
         this.dataConfiguration = dataConfiguration;
         this.worldGenRegistryManager = wrapperLookup;
         this.dimensionsRegistryManager = immutable;
      }

      public ResourceManager resourceManager() {
         return this.resourceManager;
      }

      public DataConfiguration dataConfiguration() {
         return this.dataConfiguration;
      }

      public RegistryWrapper.WrapperLookup worldGenRegistryManager() {
         return this.worldGenRegistryManager;
      }

      public DynamicRegistryManager.Immutable dimensionsRegistryManager() {
         return this.dimensionsRegistryManager;
      }
   }

   @FunctionalInterface
   public interface LoadContextSupplier {
      LoadContext get(LoadContextSupplierContext context);
   }

   public static record LoadContext(Object extraData, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
      final Object extraData;
      final DynamicRegistryManager.Immutable dimensionsRegistryManager;

      public LoadContext(Object object, DynamicRegistryManager.Immutable immutable) {
         this.extraData = object;
         this.dimensionsRegistryManager = immutable;
      }

      public Object extraData() {
         return this.extraData;
      }

      public DynamicRegistryManager.Immutable dimensionsRegistryManager() {
         return this.dimensionsRegistryManager;
      }
   }

   @FunctionalInterface
   public interface SaveApplierFactory {
      Object create(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, CombinedDynamicRegistries combinedDynamicRegistries, Object loadContext);
   }
}
