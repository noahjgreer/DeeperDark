/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.render;

import net.minecraft.util.math.ColorHelper;

public record DrawStyle(int stroke, float strokeWidth, int fill) {
    private final int stroke;
    private final float strokeWidth;
    private final int fill;
    private static final float DEFAULT_STROKE_WIDTH = 2.5f;

    public DrawStyle(int stroke, float strokeWidth, int fill) {
        this.stroke = stroke;
        this.strokeWidth = strokeWidth;
        this.fill = fill;
    }

    public static DrawStyle stroked(int stroke) {
        return new DrawStyle(stroke, 2.5f, 0);
    }

    public static DrawStyle stroked(int stroke, float strokeWidth) {
        return new DrawStyle(stroke, strokeWidth, 0);
    }

    public static DrawStyle filled(int fill) {
        return new DrawStyle(0, 0.0f, fill);
    }

    public static DrawStyle filledAndStroked(int stroke, float strokeWidth, int fill) {
        return new DrawStyle(stroke, strokeWidth, fill);
    }

    public boolean hasFill() {
        return this.fill != 0;
    }

    public boolean hasStroke() {
        return this.stroke != 0 && this.strokeWidth > 0.0f;
    }

    public int stroke(float opacity) {
        return ColorHelper.scaleAlpha((int)this.stroke, (float)opacity);
    }

    public int fill(float opacity) {
        return ColorHelper.scaleAlpha((int)this.fill, (float)opacity);
    }

    public int stroke() {
        return this.stroke;
    }

    public float strokeWidth() {
        return this.strokeWidth;
    }

    public int fill() {
        return this.fill;
    }
}

