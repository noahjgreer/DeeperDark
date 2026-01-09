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

public final class RegistryFixedCodec implements Codec {
   private final RegistryKey registry;

   public static RegistryFixedCodec of(RegistryKey registry) {
      return new RegistryFixedCodec(registry);
   }

   private RegistryFixedCodec(RegistryKey registry) {
      this.registry = registry;
   }

   public DataResult encode(RegistryEntry registryEntry, DynamicOps dynamicOps, Object object) {
      if (dynamicOps instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getOwner(this.registry);
         if (optional.isPresent()) {
            if (!registryEntry.ownerEquals((RegistryEntryOwner)optional.get())) {
               return DataResult.error(() -> {
                  return "Element " + String.valueOf(registryEntry) + " is not valid in current registry set";
               });
            }

            return (DataResult)registryEntry.getKeyOrValue().map((registryKey) -> {
               return Identifier.CODEC.encode(registryKey.getValue(), dynamicOps, object);
            }, (value) -> {
               return DataResult.error(() -> {
                  return "Elements from registry " + String.valueOf(this.registry) + " can't be serialized to a value";
               });
            });
         }
      }

      return DataResult.error(() -> {
         return "Can't access registry " + String.valueOf(this.registry);
      });
   }

   public DataResult decode(DynamicOps ops, Object input) {
      if (ops instanceof RegistryOps registryOps) {
         Optional optional = registryOps.getEntryLookup(this.registry);
         if (optional.isPresent()) {
            return Identifier.CODEC.decode(ops, input).flatMap((pair) -> {
               Identifier identifier = (Identifier)pair.getFirst();
               return ((DataResult)((RegistryEntryLookup)optional.get()).getOptional(RegistryKey.of(this.registry, identifier)).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error(() -> {
                     return "Failed to get element " + String.valueOf(identifier);
                  });
               })).map((value) -> {
                  return Pair.of(value, pair.getSecond());
               }).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error(() -> {
         return "Can't access registry " + String.valueOf(this.registry);
      });
   }

   public String toString() {
      return "RegistryFixedCodec[" + String.valueOf(this.registry) + "]";
   }

   // $FF: synthetic method
   public DataResult encode(final Object entry, final DynamicOps ops, final Object prefix) {
      return this.encode((RegistryEntry)entry, ops, prefix);
   }
}
