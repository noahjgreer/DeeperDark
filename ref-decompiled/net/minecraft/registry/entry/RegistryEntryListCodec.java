package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;

public class RegistryEntryListCodec implements Codec {
   private final RegistryKey registry;
   private final Codec entryCodec;
   private final Codec directEntryListCodec;
   private final Codec entryListStorageCodec;

   private static Codec createDirectEntryListCodec(Codec entryCodec, boolean alwaysSerializeAsList) {
      Codec codec = entryCodec.listOf().validate(Codecs.createEqualTypeChecker(RegistryEntry::getType));
      return alwaysSerializeAsList ? codec : Codecs.listOrSingle(entryCodec, codec);
   }

   public static Codec create(RegistryKey registryRef, Codec entryCodec, boolean alwaysSerializeAsList) {
      return new RegistryEntryListCodec(registryRef, entryCodec, alwaysSerializeAsList);
   }

   private RegistryEntryListCodec(RegistryKey registry, Codec entryCodec, boolean alwaysSerializeAsList) {
      this.registry = registry;
      this.entryCodec = entryCodec;
      this.directEntryListCodec = createDirectEntryListCodec(entryCodec, alwaysSerializeAsList);
      this.entryListStorageCodec = Codec.either(TagKey.codec(registry), this.directEntryListCodec);
   }

   public DataResult decode(DynamicOps ops, Object input) {
      if (ops instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getEntryLookup(this.registry);
         if (optional.isPresent()) {
            RegistryEntryLookup registryEntryLookup = (RegistryEntryLookup)optional.get();
            return this.entryListStorageCodec.decode(ops, input).flatMap((pair) -> {
               DataResult dataResult = (DataResult)((Either)pair.getFirst()).map((tag) -> {
                  return get(registryEntryLookup, tag);
               }, (entries) -> {
                  return DataResult.success(RegistryEntryList.of(entries));
               });
               return dataResult.map((entries) -> {
                  return Pair.of(entries, pair.getSecond());
               });
            });
         }
      }

      return this.decodeDirect(ops, input);
   }

   private static DataResult get(RegistryEntryLookup registry, TagKey tag) {
      return (DataResult)registry.getOptional(tag).map(DataResult::success).orElseGet(() -> {
         return DataResult.error(() -> {
            String var10000 = String.valueOf(tag.id());
            return "Missing tag: '" + var10000 + "' in '" + String.valueOf(tag.registryRef().getValue()) + "'";
         });
      });
   }

   public DataResult encode(RegistryEntryList registryEntryList, DynamicOps dynamicOps, Object object) {
      if (dynamicOps instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getOwner(this.registry);
         if (optional.isPresent()) {
            if (!registryEntryList.ownerEquals((RegistryEntryOwner)optional.get())) {
               return DataResult.error(() -> {
                  return "HolderSet " + String.valueOf(registryEntryList) + " is not valid in current registry set";
               });
            }

            return this.entryListStorageCodec.encode(registryEntryList.getStorage().mapRight(List::copyOf), dynamicOps, object);
         }
      }

      return this.encodeDirect(registryEntryList, dynamicOps, object);
   }

   private DataResult decodeDirect(DynamicOps ops, Object input) {
      return this.entryCodec.listOf().decode(ops, input).flatMap((pair) -> {
         List list = new ArrayList();
         Iterator var2 = ((List)pair.getFirst()).iterator();

         while(var2.hasNext()) {
            RegistryEntry registryEntry = (RegistryEntry)var2.next();
            if (!(registryEntry instanceof RegistryEntry.Direct)) {
               return DataResult.error(() -> {
                  return "Can't decode element " + String.valueOf(registryEntry) + " without registry";
               });
            }

            RegistryEntry.Direct direct = (RegistryEntry.Direct)registryEntry;
            list.add(direct);
         }

         return DataResult.success(new Pair(RegistryEntryList.of((List)list), pair.getSecond()));
      });
   }

   private DataResult encodeDirect(RegistryEntryList entryList, DynamicOps ops, Object prefix) {
      return this.directEntryListCodec.encode(entryList.stream().toList(), ops, prefix);
   }

   // $FF: synthetic method
   public DataResult encode(final Object entryList, final DynamicOps ops, final Object prefix) {
      return this.encode((RegistryEntryList)entryList, ops, prefix);
   }
}
