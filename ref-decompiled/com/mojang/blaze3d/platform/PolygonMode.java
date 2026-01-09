package com.mojang.blaze3d.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public enum PolygonMode {
   FILL,
   WIREFRAME;

   // $FF: synthetic method
   private static PolygonMode[] $values() {
      return new PolygonMode[]{FILL, WIREFRAME};
   }
}
