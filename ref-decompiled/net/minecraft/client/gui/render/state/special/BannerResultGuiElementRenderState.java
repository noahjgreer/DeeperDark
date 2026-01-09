package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.model.ModelPart;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BannerResultGuiElementRenderState(ModelPart flag, DyeColor baseColor, BannerPatternsComponent resultBannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public BannerResultGuiElementRenderState(ModelPart flag, DyeColor color, BannerPatternsComponent bannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
      this(flag, color, bannerPatterns, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public BannerResultGuiElementRenderState(ModelPart modelPart, DyeColor dyeColor, BannerPatternsComponent bannerPatternsComponent, int i, int j, int k, int l, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.flag = modelPart;
      this.baseColor = dyeColor;
      this.resultBannerPatterns = bannerPatternsComponent;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public float scale() {
      return 16.0F;
   }

   public ModelPart flag() {
      return this.flag;
   }

   public DyeColor baseColor() {
      return this.baseColor;
   }

   public BannerPatternsComponent resultBannerPatterns() {
      return this.resultBannerPatterns;
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

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }

   @Nullable
   public ScreenRect bounds() {
      return this.bounds;
   }
}
