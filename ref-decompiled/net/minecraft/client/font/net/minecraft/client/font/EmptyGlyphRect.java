/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
public record EmptyGlyphRect(float x, float y, float advance, float ascent, float height, Style style) implements GlyphRect
{
    public static final float DEFAULT_HEIGHT = 9.0f;
    public static final float DEFAULT_ASCENT = 7.0f;

    @Override
    public float getLeft() {
        return this.x;
    }

    @Override
    public float getTop() {
        return this.y + 7.0f - this.ascent;
    }

    @Override
    public float getRight() {
        return this.x + this.advance;
    }

    @Override
    public float getBottom() {
        return this.getTop() + this.height;
    }
}
