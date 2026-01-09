package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record PlayerSkinGuiElementRenderState(PlayerEntityModel playerModel, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public PlayerSkinGuiElementRenderState(PlayerEntityModel model, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
      this(model, texture, xRotation, yRotation, yPivot, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public PlayerSkinGuiElementRenderState(PlayerEntityModel playerEntityModel, Identifier identifier, float f, float g, float h, int i, int j, int k, int l, float m, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.playerModel = playerEntityModel;
      this.texture = identifier;
      this.xRotation = f;
      this.yRotation = g;
      this.yPivot = h;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scale = m;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public PlayerEntityModel playerModel() {
      return this.playerModel;
   }

   public Identifier texture() {
      return this.texture;
   }

   public float xRotation() {
      return this.xRotation;
   }

   public float yRotation() {
      return this.yRotation;
   }

   public float yPivot() {
      return this.yPivot;
   }

   public int x1() {
      return this.x1;
   }

   public int y1() {
      return this.y1;
   }

   public int x2() {
      return this.x2;
   }

   public int y2() {
      return this.y2;
   }

   public float scale() {
      return this.scale;
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }

   @Nullable
   public ScreenRect bounds() {
      return this.bounds;
   }
}
