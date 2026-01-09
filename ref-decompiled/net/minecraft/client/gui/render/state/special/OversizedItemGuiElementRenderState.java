package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record OversizedItemGuiElementRenderState(ItemGuiElementRenderState guiItemRenderState, int x1, int y1, int x2, int y2) implements SpecialGuiElementRenderState {
   public OversizedItemGuiElementRenderState(ItemGuiElementRenderState itemGuiElementRenderState, int i, int j, int k, int l) {
      this.guiItemRenderState = itemGuiElementRenderState;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
   }

   public float scale() {
      return 16.0F;
   }

   public Matrix3x2f pose() {
      return this.guiItemRenderState.pose();
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.guiItemRenderState.scissorArea();
   }

   @Nullable
   public ScreenRect bounds() {
      return this.guiItemRenderState.bounds();
   }

   public ItemGuiElementRenderState guiItemRenderState() {
      return this.guiItemRenderState;
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
}
