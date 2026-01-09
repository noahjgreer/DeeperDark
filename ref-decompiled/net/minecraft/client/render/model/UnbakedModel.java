package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface UnbakedModel {
   String PARTICLE_TEXTURE = "particle";

   @Nullable
   default Boolean ambientOcclusion() {
      return null;
   }

   @Nullable
   default GuiLight guiLight() {
      return null;
   }

   @Nullable
   default ModelTransformation transformations() {
      return null;
   }

   default ModelTextures.Textures textures() {
      return ModelTextures.Textures.EMPTY;
   }

   @Nullable
   default Geometry geometry() {
      return null;
   }

   @Nullable
   default Identifier parent() {
      return null;
   }

   @Environment(EnvType.CLIENT)
   public static enum GuiLight {
      ITEM("front"),
      BLOCK("side");

      private final String name;

      private GuiLight(final String name) {
         this.name = name;
      }

      public static GuiLight byName(String value) {
         GuiLight[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            GuiLight guiLight = var1[var3];
            if (guiLight.name.equals(value)) {
               return guiLight;
            }
         }

         throw new IllegalArgumentException("Invalid gui light: " + value);
      }

      public boolean isSide() {
         return this == BLOCK;
      }

      // $FF: synthetic method
      private static GuiLight[] method_36920() {
         return new GuiLight[]{ITEM, BLOCK};
      }
   }
}
