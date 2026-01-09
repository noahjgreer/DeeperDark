package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.model.Model;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SignGuiElementRenderState(Model signModel, WoodType woodType, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public SignGuiElementRenderState(Model signModel, WoodType woodType, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
      this(signModel, woodType, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public SignGuiElementRenderState(Model model, WoodType woodType, int i, int j, int k, int l, float f, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.signModel = model;
      this.woodType = woodType;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scale = f;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public Model signModel() {
      return this.signModel;
   }

   public WoodType woodType() {
      return this.woodType;
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
