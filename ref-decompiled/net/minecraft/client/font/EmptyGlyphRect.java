/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.EmptyGlyphRect
 *  net.minecraft.client.font.GlyphRect
 *  net.minecraft.text.Style
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
public record EmptyGlyphRect(float x, float y, float advance, float ascent, float height, Style style) implements GlyphRect
{
    private final float x;
    private final float y;
    private final float advance;
    private final float ascent;
    private final float height;
    private final Style style;
    public static final float DEFAULT_HEIGHT = 9.0f;
    public static final float DEFAULT_ASCENT = 7.0f;

    public EmptyGlyphRect(float x, float y, float advance, float ascent, float height, Style style) {
        this.x = x;
        this.y = y;
        this.advance = advance;
        this.ascent = ascent;
        this.height = height;
        this.style = style;
    }

    public float getLeft() {
        return this.x;
    }

    public float getTop() {
        return this.y + 7.0f - this.ascent;
    }

    public float getRight() {
        return this.x + this.advance;
    }

    public float getBottom() {
        return this.getTop() + this.height;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float advance() {
        return this.advance;
    }

    public float ascent() {
        return this.ascent;
    }

    public float height() {
        return this.height;
    }

    public Style style() {
        return this.style;
    }
}

