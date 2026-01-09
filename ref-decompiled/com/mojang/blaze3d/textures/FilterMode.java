package com.mojang.blaze3d.textures;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public enum FilterMode {
   NEAREST,
   LINEAR;

   // $FF: synthetic method
   private static FilterMode[] $values() {
      return new FilterMode[]{NEAREST, LINEAR};
   }
}
