/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.debug.chart.DebugChart
 *  net.minecraft.client.gui.hud.debug.chart.RenderingChart
 *  net.minecraft.util.profiler.log.MultiValueDebugSampleLog
 */
package net.minecraft.client.gui.hud.debug.chart;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.chart.DebugChart;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RenderingChart
extends DebugChart {
    private static final int field_45929 = 30;
    private static final double field_45930 = 33.333333333333336;

    public RenderingChart(TextRenderer textRenderer, MultiValueDebugSampleLog multiValueDebugSampleLog) {
        super(textRenderer, multiValueDebugSampleLog);
    }

    protected void renderThresholds(DrawContext context, int x, int width, int height) {
        this.drawBorderedText(context, "30 FPS", x + 1, height - 60 + 1);
        this.drawBorderedText(context, "60 FPS", x + 1, height - 30 + 1);
        context.drawHorizontalLine(x, x + width - 1, height - 30, -1);
        int i = (Integer)MinecraftClient.getInstance().options.getMaxFps().getValue();
        if (i > 0 && i <= 250) {
            context.drawHorizontalLine(x, x + width - 1, height - this.getHeight(1.0E9 / (double)i) - 1, -16711681);
        }
    }

    protected String format(double value) {
        return String.format(Locale.ROOT, "%d ms", (int)Math.round(RenderingChart.toMillisecondsPerFrame((double)value)));
    }

    protected int getHeight(double value) {
        return (int)Math.round(RenderingChart.toMillisecondsPerFrame((double)value) * 60.0 / 33.333333333333336);
    }

    protected int getColor(long value) {
        return this.getColor(RenderingChart.toMillisecondsPerFrame((double)value), 0.0, -16711936, 28.0, -256, 56.0, -65536);
    }

    private static double toMillisecondsPerFrame(double nanosecondsPerFrame) {
        return nanosecondsPerFrame / 1000000.0;
    }
}

