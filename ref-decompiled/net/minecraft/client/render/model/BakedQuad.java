package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public record BakedQuad(int[] vertexData, int tintIndex, Direction face, Sprite sprite, boolean shade, int lightEmission) {
   public BakedQuad(int[] vertexData, int tintIndex, Direction face, Sprite sprite, boolean shade, int lightEmission) {
      this.vertexData = vertexData;
      this.tintIndex = tintIndex;
      this.face = face;
      this.sprite = sprite;
      this.shade = shade;
      this.lightEmission = lightEmission;
   }

   public boolean hasTint() {
      return this.tintIndex != -1;
   }

   public int[] vertexData() {
      return this.vertexData;
   }

   public int tintIndex() {
      return this.tintIndex;
   }

   public Direction face() {
      return this.face;
   }

   public Sprite sprite() {
      return this.sprite;
   }

   public boolean shade() {
      return this.shade;
   }

   public int lightEmission() {
      return this.lightEmission;
   }
}
