package com.mojang.blaze3d.shaders;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public enum ShaderType {
   VERTEX("vertex", ".vsh"),
   FRAGMENT("fragment", ".fsh");

   private static final ShaderType[] TYPES = values();
   private final String name;
   private final String extension;

   private ShaderType(final String name, final String extension) {
      this.name = name;
      this.extension = extension;
   }

   @Nullable
   public static ShaderType byLocation(Identifier id) {
      ShaderType[] var1 = TYPES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ShaderType shaderType = var1[var3];
         if (id.getPath().endsWith(shaderType.extension)) {
            return shaderType;
         }
      }

      return null;
   }

   public String getName() {
      return this.name;
   }

   public ResourceFinder idConverter() {
      return new ResourceFinder("shaders", this.extension);
   }

   // $FF: synthetic method
   private static ShaderType[] $values() {
      return new ShaderType[]{VERTEX, FRAGMENT};
   }
}
