package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class DataPackStructureProvider implements DataProvider {
   private final DataOutput output;
   private static final Entry RELOADABLE_REGISTRY = new Entry(true, false, true);
   private static final Entry RELOADABLE_REGISTRY_WITH_TAGS = new Entry(true, true, true);
   private static final Entry DYNAMIC_REGISTRY = new Entry(true, true, false);
   private static final Entry STATIC_REGISTRY = new Entry(false, true, true);
   private static final Map RELOADABLE_REGISTRIES;
   private static final Map FAKE_REGISTRIES;
   static final Codec REGISTRY_KEY_CODEC;

   public DataPackStructureProvider(DataOutput output) {
      this.output = output;
   }

   public CompletableFuture run(DataWriter writer) {
      Registries registries = new Registries(this.buildEntries(), FAKE_REGISTRIES);
      Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("datapack.json");
      return DataProvider.writeToPath(writer, (JsonElement)DataPackStructureProvider.Registries.CODEC.encodeStart(JsonOps.INSTANCE, registries).getOrThrow(), path);
   }

   public String getName() {
      return "Datapack Structure";
   }

   private void addEntry(Map map, RegistryKey key, Entry entry) {
      Entry entry2 = (Entry)map.putIfAbsent(key, entry);
      if (entry2 != null) {
         throw new IllegalStateException("Duplicate entry for key " + String.valueOf(key.getValue()));
      }
   }

   private Map buildEntries() {
      Map map = new HashMap();
      net.minecraft.registry.Registries.REGISTRIES.forEach((registry) -> {
         this.addEntry(map, registry.getKey(), STATIC_REGISTRY);
      });
      RegistryLoader.DYNAMIC_REGISTRIES.forEach((registry) -> {
         this.addEntry(map, registry.key(), DYNAMIC_REGISTRY);
      });
      RegistryLoader.DIMENSION_REGISTRIES.forEach((registry) -> {
         this.addEntry(map, registry.key(), DYNAMIC_REGISTRY);
      });
      RELOADABLE_REGISTRIES.forEach((key, entry) -> {
         this.addEntry(map, key, entry);
      });
      return map;
   }

   static {
      RELOADABLE_REGISTRIES = Map.of(RegistryKeys.RECIPE, RELOADABLE_REGISTRY, RegistryKeys.ADVANCEMENT, RELOADABLE_REGISTRY, RegistryKeys.LOOT_TABLE, RELOADABLE_REGISTRY_WITH_TAGS, RegistryKeys.ITEM_MODIFIER, RELOADABLE_REGISTRY_WITH_TAGS, RegistryKeys.PREDICATE, RELOADABLE_REGISTRY_WITH_TAGS);
      FAKE_REGISTRIES = Map.of("structure", new FakeRegistry(DataPackStructureProvider.Format.STRUCTURE, new Entry(true, false, true)), "function", new FakeRegistry(DataPackStructureProvider.Format.MCFUNCTION, new Entry(true, true, true)));
      REGISTRY_KEY_CODEC = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
   }

   private static record Registries(Map registries, Map others) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.unboundedMap(DataPackStructureProvider.REGISTRY_KEY_CODEC, DataPackStructureProvider.Entry.CODEC).fieldOf("registries").forGetter(Registries::registries), Codec.unboundedMap(Codec.STRING, DataPackStructureProvider.FakeRegistry.CODEC).fieldOf("others").forGetter(Registries::others)).apply(instance, Registries::new);
      });

      Registries(Map map, Map map2) {
         this.registries = map;
         this.others = map2;
      }

      public Map registries() {
         return this.registries;
      }

      public Map others() {
         return this.others;
      }
   }

   static record Entry(boolean elements, boolean tags, boolean stable) {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.BOOL.fieldOf("elements").forGetter(Entry::elements), Codec.BOOL.fieldOf("tags").forGetter(Entry::tags), Codec.BOOL.fieldOf("stable").forGetter(Entry::stable)).apply(instance, Entry::new);
      });
      public static final Codec CODEC;

      Entry(boolean bl, boolean bl2, boolean bl3) {
         this.elements = bl;
         this.tags = bl2;
         this.stable = bl3;
      }

      public boolean elements() {
         return this.elements;
      }

      public boolean tags() {
         return this.tags;
      }

      public boolean stable() {
         return this.stable;
      }

      static {
         CODEC = MAP_CODEC.codec();
      }
   }

   private static record FakeRegistry(Format format, Entry entry) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(DataPackStructureProvider.Format.CODEC.fieldOf("format").forGetter(FakeRegistry::format), DataPackStructureProvider.Entry.MAP_CODEC.forGetter(FakeRegistry::entry)).apply(instance, FakeRegistry::new);
      });

      FakeRegistry(Format format, Entry entry) {
         this.format = format;
         this.entry = entry;
      }

      public Format format() {
         return this.format;
      }

      public Entry entry() {
         return this.entry;
      }
   }

   private static enum Format implements StringIdentifiable {
      STRUCTURE("structure"),
      MCFUNCTION("mcfunction");

      public static final Codec CODEC = StringIdentifiable.createCodec(Format::values);
      private final String id;

      private Format(final String id) {
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Format[] method_62725() {
         return new Format[]{STRUCTURE, MCFUNCTION};
      }
   }
}
