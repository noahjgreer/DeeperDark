package net.minecraft.data.loottable;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.random.RandomSequence;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DataOutput.PathResolver pathResolver;
   private final Set lootTableIds;
   private final List lootTypeGenerators;
   private final CompletableFuture registriesFuture;

   public LootTableProvider(DataOutput output, Set lootTableIds, List lootTypeGenerators, CompletableFuture registriesFuture) {
      this.pathResolver = output.getResolver(RegistryKeys.LOOT_TABLE);
      this.lootTypeGenerators = lootTypeGenerators;
      this.lootTableIds = lootTableIds;
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      return this.registriesFuture.thenCompose((registries) -> {
         return this.run(writer, registries);
      });
   }

   private CompletableFuture run(DataWriter writer, RegistryWrapper.WrapperLookup registries) {
      MutableRegistry mutableRegistry = new SimpleRegistry(RegistryKeys.LOOT_TABLE, Lifecycle.experimental());
      Map map = new Object2ObjectOpenHashMap();
      this.lootTypeGenerators.forEach((lootTypeGenerator) -> {
         ((LootTableGenerator)lootTypeGenerator.provider().apply(registries)).accept((lootTable, builder) -> {
            Identifier identifier = getId(lootTable);
            Identifier identifier2 = (Identifier)map.put(RandomSequence.createSeed(identifier), identifier);
            if (identifier2 != null) {
               String var10000 = String.valueOf(identifier2);
               Util.logErrorOrPause("Loot table random sequence seed collision on " + var10000 + " and " + String.valueOf(lootTable.getValue()));
            }

            builder.randomSequenceId(identifier);
            LootTable lootTable2 = builder.type(lootTypeGenerator.paramSet).build();
            mutableRegistry.add(lootTable, lootTable2, RegistryEntryInfo.DEFAULT);
         });
      });
      mutableRegistry.freeze();
      ErrorReporter.Impl impl = new ErrorReporter.Impl();
      RegistryEntryLookup.RegistryLookup registryLookup = (new DynamicRegistryManager.ImmutableImpl(List.of(mutableRegistry))).toImmutable();
      LootTableReporter lootTableReporter = new LootTableReporter(impl, LootContextTypes.GENERIC, registryLookup);
      Set set = Sets.difference(this.lootTableIds, mutableRegistry.getKeys());
      Iterator var9 = set.iterator();

      while(var9.hasNext()) {
         RegistryKey registryKey = (RegistryKey)var9.next();
         impl.report(new MissingTableError(registryKey));
      }

      mutableRegistry.streamEntries().forEach((entry) -> {
         ((LootTable)entry.value()).validate(lootTableReporter.withContextType(((LootTable)entry.value()).getType()).makeChild(new ErrorReporter.LootTableContext(entry.registryKey()), entry.registryKey()));
      });
      if (!impl.isEmpty()) {
         impl.apply((name, error) -> {
            LOGGER.warn("Found validation problem in {}: {}", name, error.getMessage());
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf((CompletableFuture[])mutableRegistry.getEntrySet().stream().map((entry) -> {
            RegistryKey registryKey = (RegistryKey)entry.getKey();
            LootTable lootTable = (LootTable)entry.getValue();
            Path path = this.pathResolver.resolveJson(registryKey.getValue());
            return DataProvider.writeCodecToPath(writer, (RegistryWrapper.WrapperLookup)registries, LootTable.CODEC, lootTable, path);
         }).toArray((i) -> {
            return new CompletableFuture[i];
         }));
      }
   }

   private static Identifier getId(RegistryKey lootTableKey) {
      return lootTableKey.getValue();
   }

   public String getName() {
      return "Loot Tables";
   }

   public static record MissingTableError(RegistryKey id) implements ErrorReporter.Error {
      public MissingTableError(RegistryKey registryKey) {
         this.id = registryKey;
      }

      public String getMessage() {
         return "Missing built-in table: " + String.valueOf(this.id.getValue());
      }

      public RegistryKey id() {
         return this.id;
      }
   }

   public static record LootTypeGenerator(Function provider, ContextType paramSet) {
      final ContextType paramSet;

      public LootTypeGenerator(Function function, ContextType contextType) {
         this.provider = function;
         this.paramSet = contextType;
      }

      public Function provider() {
         return this.provider;
      }

      public ContextType paramSet() {
         return this.paramSet;
      }
   }
}
