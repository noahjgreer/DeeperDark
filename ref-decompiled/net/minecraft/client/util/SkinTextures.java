package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SkinTextures(Identifier texture, @Nullable String textureUrl, @Nullable Identifier capeTexture, @Nullable Identifier elytraTexture, Model model, boolean secure) {
   public SkinTextures(Identifier identifier, @Nullable String string, @Nullable Identifier identifier2, @Nullable Identifier identifier3, Model model, boolean bl) {
      this.texture = identifier;
      this.textureUrl = string;
      this.capeTexture = identifier2;
      this.elytraTexture = identifier3;
      this.model = model;
      this.secure = bl;
   }

   public Identifier texture() {
      return this.texture;
   }

   @Nullable
   public String textureUrl() {
      return this.textureUrl;
   }

   @Nullable
   public Identifier capeTexture() {
      return this.capeTexture;
   }

   @Nullable
   public Identifier elytraTexture() {
      return this.elytraTexture;
   }

   public Model model() {
      return this.model;
   }

   public boolean secure() {
      return this.secure;
   }

   @Environment(EnvType.CLIENT)
   public static enum Model {
      SLIM("slim"),
      WIDE("default");

      private final String name;

      private Model(final String name) {
         this.name = name;
      }

      public static Model fromName(@Nullable String name) {
         if (name == null) {
            return WIDE;
         } else {
            Model var10000;
            switch (name) {
               case "slim":
                  var10000 = SLIM;
                  break;
               default:
                  var10000 = WIDE;
            }

            return var10000;
         }
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Model[] method_47439() {
         return new Model[]{SLIM, WIDE};
      }
   }
}
