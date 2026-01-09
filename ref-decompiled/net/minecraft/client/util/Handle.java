package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Handle {
   Handle EMPTY = () -> {
      throw new IllegalStateException("Cannot dereference handle with no underlying resource");
   };

   static Handle empty() {
      return EMPTY;
   }

   Object get();
}
