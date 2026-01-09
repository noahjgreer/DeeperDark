package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class RegistryCloner {
   private final Codec elementCodec;

   RegistryCloner(Codec elementCodec) {
      this.elementCodec = elementCodec;
   }

   public Object clone(Object value, RegistryWrapper.WrapperLookup subsetRegistry, RegistryWrapper.WrapperLookup fullRegistry) {
      DynamicOps dynamicOps = subsetRegistry.getOps(JavaOps.INSTANCE);
      DynamicOps dynamicOps2 = fullRegistry.getOps(JavaOps.INSTANCE);
      Object object = this.elementCodec.encodeStart(dynamicOps, value).getOrThrow((error) -> {
         return new IllegalStateException("Failed to encode: " + error);
      });
      return this.elementCodec.parse(dynamicOps2, object).getOrThrow((error) -> {
         return new IllegalStateException("Failed to decode: " + error);
      });
   }

   public static class CloneableRegistries {
      private final Map registries = new HashMap();

      public CloneableRegistries add(RegistryKey registryRef, Codec elementCodec) {
         this.registries.put(registryRef, new RegistryCloner(elementCodec));
         return this;
      }

      @Nullable
      public RegistryCloner get(RegistryKey registryRef) {
         return (RegistryCloner)this.registries.get(registryRef);
      }
   }
}
