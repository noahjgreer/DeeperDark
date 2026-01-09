package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record EntityGuiElementRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public EntityGuiElementRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
      this(renderState, translation, rotation, overrideCameraAngle, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public EntityGuiElementRenderState(EntityRenderState entityRenderState, Vector3f vector3f, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, int i, int j, int k, int l, float f, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.renderState = entityRenderState;
      this.translation = vector3f;
      this.rotation = quaternionf;
      this.overrideCameraAngle = quaternionf2;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scale = f;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public EntityRenderState renderState() {
      return this.renderState;
   }

   public Vector3f translation() {
      return this.translation;
   }

   public Quaternionf rotation() {
      return this.rotation;
   }

   @Nullable
   public Quaternionf overrideCameraAngle() {
      return this.overrideCameraAngle;
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
