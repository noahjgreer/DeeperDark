package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum UniformType {
   UNIFORM_BUFFER("ubo"),
   TEXEL_BUFFER("utb");

   final String name;

   private UniformType(final String name) {
      this.name = name;
   }

   // $FF: synthetic method
   private static UniformType[] method_67774() {
      return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
   }
}
