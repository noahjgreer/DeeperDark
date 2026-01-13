/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.debug.chart.DebugChart
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.profiler.log.MultiValueDebugSampleLog
 */
package net.minecraft.client.gui.hud.debug.chart;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

@Environment(value=EnvType.CLIENT)
public abstract class DebugChart {
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
        long m = Integer.MAX_VALUE;
        long n = Integer.MIN_VALUE;
        int j = Math.max(0, this.log.getDimension() - (width - 2));
        int k = this.log.getLength() - j;
        for (int o = 0; o < k; ++o) {
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
            String string = this.format((double)m) + " min";
            String string2 = this.format((double)l / (double)k) + " avg";
            String string3 = this.format((double)n) + " max";
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, string, x + 2, i - 60 - 9, -2039584);
            int n2 = x + width / 2;
            Objects.requireNonNull(this.textRenderer);
            context.drawCenteredTextWithShadow(this.textRenderer, string2, n2, i - 60 - 9, -2039584);
            int n3 = x + width - this.textRenderer.getWidth(string3) - 2;
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, string3, n3, i - 60 - 9, -2039584);
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
        int n = x + this.textRenderer.getWidth(string) + 1;
        Objects.requireNonNull(this.textRenderer);
        context.fill(x, y, n, y + 9, -1873784752);
        context.drawText(this.textRenderer, string, x + 1, y + 1, -2039584, false);
    }

    protected abstract String format(double var1);

    protected abstract int getHeight(double var1);

    protected abstract int getColor(long var1);

    protected int getColor(double value, double min, int minColor, double median, int medianColor, double max, int maxColor) {
        if ((value = MathHelper.clamp((double)value, (double)min, (double)max)) < median) {
            return ColorHelper.lerp((float)((float)((value - min) / (median - min))), (int)minColor, (int)medianColor);
        }
        return ColorHelper.lerp((float)((float)((value - median) / (max - median))), (int)medianColor, (int)maxColor);
    }
}

