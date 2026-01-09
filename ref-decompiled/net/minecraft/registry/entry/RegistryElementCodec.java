package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

public final class RegistryElementCodec implements Codec {
   private final RegistryKey registryRef;
   private final Codec elementCodec;
   private final boolean allowInlineDefinitions;

   public static RegistryElementCodec of(RegistryKey registryRef, Codec elementCodec) {
      return of(registryRef, elementCodec, true);
   }

   public static RegistryElementCodec of(RegistryKey registryRef, Codec elementCodec, boolean allowInlineDefinitions) {
      return new RegistryElementCodec(registryRef, elementCodec, allowInlineDefinitions);
   }

   private RegistryElementCodec(RegistryKey registryRef, Codec elementCodec, boolean allowInlineDefinitions) {
      this.registryRef = registryRef;
      this.elementCodec = elementCodec;
      this.allowInlineDefinitions = allowInlineDefinitions;
   }

   public DataResult encode(RegistryEntry registryEntry, DynamicOps dynamicOps, Object object) {
      if (dynamicOps instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getOwner(this.registryRef);
         if (optional.isPresent()) {
            if (!registryEntry.ownerEquals((RegistryEntryOwner)optional.get())) {
               return DataResult.error(() -> {
                  return "Element " + String.valueOf(registryEntry) + " is not valid in current registry set";
               });
            }

            return (DataResult)registryEntry.getKeyOrValue().map((key) -> {
               return Identifier.CODEC.encode(key.getValue(), dynamicOps, object);
            }, (value) -> {
               return this.elementCodec.encode(value, dynamicOps, object);
            });
         }
      }

      return this.elementCodec.encode(registryEntry.value(), dynamicOps, object);
   }

   public DataResult decode(DynamicOps ops, Object input) {
      if (ops instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getEntryLookup(this.registryRef);
         if (optional.isEmpty()) {
            return DataResult.error(() -> {
               return "Registry does not exist: " + String.valueOf(this.registryRef);
            });
         } else {
            RegistryEntryLookup registryEntryLookup = (RegistryEntryLookup)optional.get();
            DataResult dataResult = Identifier.CODEC.decode(ops, input);
            if (dataResult.result().isEmpty()) {
               return !this.allowInlineDefinitions ? DataResult.error(() -> {
                  return "Inline definitions not allowed here";
               }) : this.elementCodec.decode(ops, input).map((pairx) -> {
                  return pairx.mapFirst(RegistryEntry::of);
               });
            } else {
               Pair pair = (Pair)dataResult.result().get();
               RegistryKey registryKey = RegistryKey.of(this.registryRef, (Identifier)pair.getFirst());
               return ((DataResult)registryEntryLookup.getOptional(registryKey).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error(() -> {
                     return "Failed to get element " + String.valueOf(registryKey);
                  });
               })).map((reference) -> {
                  return Pair.of(reference, pair.getSecond());
               }).setLifecycle(Lifecycle.stable());
            }
         }
      } else {
         return this.elementCodec.decode(ops, input).map((pairx) -> {
            return pairx.mapFirst(RegistryEntry::of);
         });
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.registryRef);
      return "RegistryFileCodec[" + var10000 + " " + String.valueOf(this.elementCodec) + "]";
   }

   // $FF: synthetic method
   public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
      return this.encode((RegistryEntry)input, ops, prefix);
   }
}
