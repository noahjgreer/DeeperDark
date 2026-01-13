/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.debug.chart.DebugChart
 *  net.minecraft.client.gui.hud.debug.chart.PacketSizeChart
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.profiler.log.MultiValueDebugSampleLog
 */
package net.minecraft.client.gui.hud.debug.chart;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.chart.DebugChart;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PacketSizeChart
extends DebugChart {
    private static final int field_45920 = -16711681;
    private static final int field_45921 = -6250241;
    private static final int field_45922 = -65536;
    private static final int field_45923 = 1024;
    private static final int field_45924 = 0x100000;
    private static final int field_45925 = 0x100000;

    public PacketSizeChart(TextRenderer textRenderer, MultiValueDebugSampleLog multiValueDebugSampleLog) {
        super(textRenderer, multiValueDebugSampleLog);
    }

    protected void renderThresholds(DrawContext context, int x, int width, int height) {
        this.drawSizeBar(context, x, width, height, 64);
        this.drawSizeBar(context, x, width, height, 1024);
        this.drawSizeBar(context, x, width, height, 16384);
        this.drawBorderedText(context, PacketSizeChart.formatBytesPerSecond((double)1048576.0), x + 1, height - PacketSizeChart.calculateHeight((double)1048576.0) + 1);
    }

    private void drawSizeBar(DrawContext context, int x, int width, int height, int bytes) {
        this.drawSizeBar(context, x, width, height - PacketSizeChart.calculateHeight((double)bytes), PacketSizeChart.formatBytesPerSecond((double)bytes));
    }

    private void drawSizeBar(DrawContext context, int x, int width, int y, String label) {
        this.drawBorderedText(context, label, x + 1, y + 1);
        context.drawHorizontalLine(x, x + width - 1, y, -1);
    }

    protected String format(double value) {
        return PacketSizeChart.formatBytesPerSecond((double)PacketSizeChart.toBytesPerSecond((double)value));
    }

    private static String formatBytesPerSecond(double value) {
        if (value >= 1048576.0) {
            return String.format(Locale.ROOT, "%.1f MiB/s", value / 1048576.0);
        }
        if (value >= 1024.0) {
            return String.format(Locale.ROOT, "%.1f KiB/s", value / 1024.0);
        }
        return String.format(Locale.ROOT, "%d B/s", MathHelper.floor((double)value));
    }

    protected int getHeight(double value) {
        return PacketSizeChart.calculateHeight((double)PacketSizeChart.toBytesPerSecond((double)value));
    }

    private static int calculateHeight(double value) {
        return (int)Math.round(Math.log(value + 1.0) * 60.0 / Math.log(1048576.0));
    }

    protected int getColor(long value) {
        return this.getColor(PacketSizeChart.toBytesPerSecond((double)value), 0.0, -16711681, 8192.0, -6250241, 1.048576E7, -65536);
    }

    private static double toBytesPerSecond(double bytesPerTick) {
        return bytesPerTick * 20.0;
    }
}

