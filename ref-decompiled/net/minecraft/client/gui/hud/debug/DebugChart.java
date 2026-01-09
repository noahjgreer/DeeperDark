package net.minecraft.client.gui.hud.debug;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

@Environment(EnvType.CLIENT)
public abstract class DebugChart {
   protected static final int TEXT_COLOR = -2039584;
   protected static final int field_45916 = 60;
   protected static final int field_45917 = 1;
   protected final TextRenderer textRenderer;
   protected final MultiValueDebugSampleLog log;

   protected DebugChart(TextRenderer textRenderer, MultiValueDebugSampleLog log) {
      this.textRenderer = textRenderer;
      this.log = log;
   }

   public int getWidth(int centerX) {
      return Math.min(this.log.getDimension() + 2, centerX);
   }

   public int getHeight() {
      Objects.requireNonNull(this.textRenderer);
      return 60 + 9;
   }

   public void render(DrawContext context, int x, int width) {
      int i = context.getScaledWindowHeight();
      context.fill(x, i - 60, x + width, i, -1873784752);
      long l = 0L;
      long m = 2147483647L;
      long n = -2147483648L;
      int j = Math.max(0, this.log.getDimension() - (width - 2));
      int k = this.log.getLength() - j;

      for(int o = 0; o < k; ++o) {
         int p = x + o + 1;
         int q = j + o;
         long r = this.get(q);
         m = Math.min(m, r);
         n = Math.max(n, r);
         l += r;
         this.drawBar(context, i, p, q);
      }

      context.drawHorizontalLine(x, x + width - 1, i - 60, -1);
      context.drawHorizontalLine(x, x + width - 1, i - 1, -1);
      context.drawVerticalLine(x, i - 60, i, -1);
      context.drawVerticalLine(x + width - 1, i - 60, i, -1);
      if (k > 0) {
         String var10000 = this.format((double)m);
         String string = var10000 + " min";
         var10000 = this.format((double)l / (double)k);
         String string2 = var10000 + " avg";
         var10000 = this.format((double)n);
         String string3 = var10000 + " max";
         TextRenderer var10001 = this.textRenderer;
         int var10003 = x + 2;
         int var10004 = i - 60;
         Objects.requireNonNull(this.textRenderer);
         context.drawTextWithShadow(var10001, string, var10003, var10004 - 9, -2039584);
         var10001 = this.textRenderer;
         var10003 = x + width / 2;
         var10004 = i - 60;
         Objects.requireNonNull(this.textRenderer);
         context.drawCenteredTextWithShadow(var10001, string2, var10003, var10004 - 9, -2039584);
         var10001 = this.textRenderer;
         var10003 = x + width - this.textRenderer.getWidth(string3) - 2;
         var10004 = i - 60;
         Objects.requireNonNull(this.textRenderer);
         context.drawTextWithShadow(var10001, string3, var10003, var10004 - 9, -2039584);
      }

      this.renderThresholds(context, x, width, i);
   }

   protected void drawBar(DrawContext context, int y, int x, int index) {
      this.drawTotalBar(context, y, x, index);
      this.drawOverlayBar(context, y, x, index);
   }

   protected void drawTotalBar(DrawContext context, int y, int x, int index) {
      long l = this.log.get(index);
      int i = this.getHeight((double)l);
      int j = this.getColor(l);
      context.fill(x, y - i, x + 1, y, j);
   }

   protected void drawOverlayBar(DrawContext context, int y, int x, int index) {
   }

   protected long get(int index) {
      return this.log.get(index);
   }

   protected void renderThresholds(DrawContext context, int x, int width, int height) {
   }

   protected void drawBorderedText(DrawContext context, String string, int x, int y) {
      int var10003 = x + this.textRenderer.getWidth(string) + 1;
      Objects.requireNonNull(this.textRenderer);
      context.fill(x, y, var10003, y + 9, -1873784752);
      context.drawText(this.textRenderer, string, x + 1, y + 1, -2039584, false);
   }

   protected abstract String format(double value);

   protected abstract int getHeight(double value);

   protected abstract int getColor(long value);

   protected int getColor(double value, double min, int minColor, double median, int medianColor, double max, int maxColor) {
      value = MathHelper.clamp(value, min, max);
      return value < median ? ColorHelper.lerp((float)((value - min) / (median - min)), minColor, medianColor) : ColorHelper.lerp((float)((value - median) / (max - median)), medianColor, maxColor);
   }
}
