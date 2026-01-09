package net.minecraft.registry;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public interface Registry extends Keyable, RegistryWrapper.Impl, IndexedIterable, FabricRegistry {
   RegistryKey getKey();

   default Codec getCodec() {
      return this.getReferenceEntryCodec().flatComapMap(RegistryEntry.Reference::value, (value) -> {
         return this.validateReference(this.getEntry(value));
      });
   }

   default Codec getEntryCodec() {
      return this.getReferenceEntryCodec().flatComapMap((entry) -> {
         return entry;
      }, this::validateReference);
   }

   private Codec getReferenceEntryCodec() {
      Codec codec = Identifier.CODEC.comapFlatMap((id) -> {
         return (DataResult)this.getEntry(id).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               String var10000 = String.valueOf(this.getKey());
               return "Unknown registry key in " + var10000 + ": " + String.valueOf(id);
            });
         });
      }, (entry) -> {
         return entry.registryKey().getValue();
      });
      return Codecs.withLifecycle(codec, (entry) -> {
         return (Lifecycle)this.getEntryInfo(entry.registryKey()).map(RegistryEntryInfo::lifecycle).orElse(Lifecycle.experimental());
      });
   }

   private DataResult validateReference(RegistryEntry entry) {
      DataResult var10000;
      if (entry instanceof RegistryEntry.Reference reference) {
         var10000 = DataResult.success(reference);
      } else {
         var10000 = DataResult.error(() -> {
            String var10000 = String.valueOf(this.getKey());
            return "Unregistered holder in " + var10000 + ": " + String.valueOf(entry);
         });
      }

      return var10000;
   }

   default Stream keys(DynamicOps ops) {
      return this.getIds().stream().map((id) -> {
         return ops.createString(id.toString());
      });
   }

   @Nullable
   Identifier getId(Object value);

   Optional getKey(Object entry);

   int getRawId(@Nullable Object value);

   @Nullable
   Object get(@Nullable RegistryKey key);

   @Nullable
   Object get(@Nullable Identifier id);

   Optional getEntryInfo(RegistryKey key);

   default Optional getOptionalValue(@Nullable Identifier id) {
      return Optional.ofNullable(this.get(id));
   }

   default Optional getOptionalValue(@Nullable RegistryKey key) {
      return Optional.ofNullable(this.get(key));
   }

   Optional getDefaultEntry();

   default Object getValueOrThrow(RegistryKey key) {
      Object object = this.get(key);
      if (object == null) {
         String var10002 = String.valueOf(this.getKey());
         throw new IllegalStateException("Missing key in " + var10002 + ": " + String.valueOf(key));
      } else {
         return object;
      }
   }

   Set getIds();

   Set getEntrySet();

   Set getKeys();

   Optional getRandom(Random random);

   default Stream stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsId(Identifier id);

   boolean contains(RegistryKey key);

   static Object register(Registry registry, String id, Object entry) {
      return register(registry, Identifier.of(id), entry);
   }

   static Object register(Registry registry, Identifier id, Object entry) {
      return register(registry, RegistryKey.of(registry.getKey(), id), entry);
   }

   static Object register(Registry registry, RegistryKey key, Object entry) {
      ((MutableRegistry)registry).add(key, entry, RegistryEntryInfo.DEFAULT);
      return entry;
   }

   static RegistryEntry.Reference registerReference(Registry registry, RegistryKey key, Object entry) {
      return ((MutableRegistry)registry).add(key, entry, RegistryEntryInfo.DEFAULT);
   }

   static RegistryEntry.Reference registerReference(Registry registry, Identifier id, Object entry) {
      return registerReference(registry, RegistryKey.of(registry.getKey(), id), entry);
   }

   Registry freeze();

   RegistryEntry.Reference createEntry(Object value);

   Optional getEntry(int rawId);

   Optional getEntry(Identifier id);

   RegistryEntry getEntry(Object value);

   default Iterable iterateEntries(TagKey tag) {
      return (Iterable)DataFixUtils.orElse(this.getOptional(tag), List.of());
   }

   default Optional getRandomEntry(TagKey tag, Random random) {
      return this.getOptional(tag).flatMap((entryList) -> {
         return entryList.getRandom(random);
      });
   }

   Stream streamTags();

   default IndexedIterable getIndexedEntries() {
      return new IndexedIterable() {
         public int getRawId(RegistryEntry registryEntry) {
            return Registry.this.getRawId(registryEntry.value());
         }

         @Nullable
         public RegistryEntry get(int i) {
            return (RegistryEntry)Registry.this.getEntry(i).orElse((Object)null);
         }

         public int size() {
            return Registry.this.size();
         }

         public Iterator iterator() {
            return Registry.this.streamEntries().map((entry) -> {
               return entry;
            }).iterator();
         }

         // $FF: synthetic method
         @Nullable
         public Object get(final int index) {
            return this.get(index);
         }
      };
   }

   PendingTagLoad startTagReload(TagGroupLoader.RegistryTags tags);

   public interface PendingTagLoad {
      RegistryKey getKey();

      RegistryWrapper.Impl getLookup();

      void apply();

      int size();
   }
}
