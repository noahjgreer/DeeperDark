package com.mojang.blaze3d.textures;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public enum AddressMode {
   REPEAT,
   CLAMP_TO_EDGE;

   // $FF: synthetic method
   private static AddressMode[] $values() {
      return new AddressMode[]{REPEAT, CLAMP_TO_EDGE};
   }
}
