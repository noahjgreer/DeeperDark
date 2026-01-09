package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record SpriteDimensions(int width, int height) {
   public SpriteDimensions(int i, int j) {
      this.width = i;
      this.height = j;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
