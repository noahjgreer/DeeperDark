package net.minecraft.registry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class ReloadableRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());

   public static CompletableFuture reload(CombinedDynamicRegistries dynamicRegistries, List pendingTagLoads, ResourceManager resourceManager, Executor prepareExecutor) {
      List list = TagGroupLoader.collectRegistries(dynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE), pendingTagLoads);
      RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(list.stream());
      RegistryOps registryOps = wrapperLookup.getOps(JsonOps.INSTANCE);
      List list2 = LootDataType.stream().map((type) -> {
         return prepare(type, registryOps, resourceManager, prepareExecutor);
      }).toList();
      CompletableFuture completableFuture = Util.combineSafe(list2);
      return completableFuture.thenApplyAsync((registries) -> {
         return toResult(dynamicRegistries, wrapperLookup, registries);
      }, prepareExecutor);
   }

   private static CompletableFuture prepare(LootDataType type, RegistryOps ops, ResourceManager resourceManager, Executor prepareExecutor) {
      return CompletableFuture.supplyAsync(() -> {
         MutableRegistry mutableRegistry = new SimpleRegistry(type.registryKey(), Lifecycle.experimental());
         Map map = new HashMap();
         JsonDataLoader.load(resourceManager, (RegistryKey)type.registryKey(), ops, type.codec(), map);
         map.forEach((id, value) -> {
            mutableRegistry.add(RegistryKey.of(type.registryKey(), id), value, DEFAULT_REGISTRY_ENTRY_INFO);
         });
         TagGroupLoader.loadInitial(resourceManager, mutableRegistry);
         return mutableRegistry;
      }, prepareExecutor);
   }

   private static ReloadResult toResult(CombinedDynamicRegistries dynamicRegistries, RegistryWrapper.WrapperLookup nonReloadables, List registries) {
      CombinedDynamicRegistries combinedDynamicRegistries = with(dynamicRegistries, registries);
      RegistryWrapper.WrapperLookup wrapperLookup = concat(nonReloadables, combinedDynamicRegistries.get(ServerDynamicRegistryType.RELOADABLE));
      validate(wrapperLookup);
      return new ReloadResult(combinedDynamicRegistries, wrapperLookup);
   }

   private static RegistryWrapper.WrapperLookup concat(RegistryWrapper.WrapperLookup first, RegistryWrapper.WrapperLookup second) {
      return RegistryWrapper.WrapperLookup.of(Stream.concat(first.stream(), second.stream()));
   }

   private static void validate(RegistryWrapper.WrapperLookup registries) {
      ErrorReporter.Impl impl = new ErrorReporter.Impl();
      LootTableReporter lootTableReporter = new LootTableReporter(impl, LootContextTypes.GENERIC, registries);
      LootDataType.stream().forEach((type) -> {
         validateLootData(lootTableReporter, type, registries);
      });
      impl.apply((id, error) -> {
         LOGGER.warn("Found loot table element validation problem in {}: {}", id, error.getMessage());
      });
   }

   private static CombinedDynamicRegistries with(CombinedDynamicRegistries dynamicRegistries, List registries) {
      return dynamicRegistries.with(ServerDynamicRegistryType.RELOADABLE, (DynamicRegistryManager.Immutable[])((new DynamicRegistryManager.ImmutableImpl(registries)).toImmutable()));
   }

   private static void validateLootData(LootTableReporter reporter, LootDataType lootDataType, RegistryWrapper.WrapperLookup registries) {
      RegistryWrapper registryWrapper = registries.getOrThrow(lootDataType.registryKey());
      registryWrapper.streamEntries().forEach((entry) -> {
         lootDataType.validate(reporter, entry.registryKey(), entry.value());
      });
   }

   public static record ReloadResult(CombinedDynamicRegistries layers, RegistryWrapper.WrapperLookup lookupWithUpdatedTags) {
      public ReloadResult(CombinedDynamicRegistries combinedDynamicRegistries, RegistryWrapper.WrapperLookup wrapperLookup) {
         this.layers = combinedDynamicRegistries;
         this.lookupWithUpdatedTags = wrapperLookup;
      }

      public CombinedDynamicRegistries layers() {
         return this.layers;
      }

      public RegistryWrapper.WrapperLookup lookupWithUpdatedTags() {
         return this.lookupWithUpdatedTags;
      }
   }

   public static class Lookup {
      private final RegistryWrapper.WrapperLookup registries;

      public Lookup(RegistryWrapper.WrapperLookup registries) {
         this.registries = registries;
      }

      public RegistryWrapper.WrapperLookup createRegistryLookup() {
         return this.registries;
      }

      public LootTable getLootTable(RegistryKey key) {
         return (LootTable)this.registries.getOptional(RegistryKeys.LOOT_TABLE).flatMap((registryEntryLookup) -> {
            return registryEntryLookup.getOptional(key);
         }).map(RegistryEntry::value).orElse(LootTable.EMPTY);
      }
   }
}
