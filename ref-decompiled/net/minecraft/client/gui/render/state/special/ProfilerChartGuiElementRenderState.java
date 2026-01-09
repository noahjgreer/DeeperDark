package net.minecraft.client.gui.render.state.special;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ProfilerChartGuiElementRenderState(List chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public ProfilerChartGuiElementRenderState(List chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
      this(chartData, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public ProfilerChartGuiElementRenderState(List list, int i, int j, int k, int l, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.chartData = list;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public float scale() {
      return 1.0F;
   }

   public List chartData() {
      return this.chartData;
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
