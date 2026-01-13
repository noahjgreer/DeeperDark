/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.DrawnSpriteGlyph
 *  net.minecraft.client.font.TextDrawable$DrawnGlyphRect
 *  net.minecraft.client.render.VertexConsumer
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public interface DrawnSpriteGlyph
extends TextDrawable.DrawnGlyphRect {
    public static final float field_63888 = 8.0f;
    public static final float field_63889 = 8.0f;
    public static final float field_63890 = 8.0f;

    default public void render(Matrix4f matrix4f, VertexConsumer consumer, int light, boolean noDepth) {
        float f = 0.0f;
        if (this.shadowColor() != 0) {
            this.draw(matrix4f, consumer, light, this.shadowOffset(), this.shadowOffset(), 0.0f, this.shadowColor());
            if (!noDepth) {
                f += 0.03f;
            }
        }
        this.draw(matrix4f, consumer, light, 0.0f, 0.0f, f, this.color());
    }

    public void draw(Matrix4f var1, VertexConsumer var2, int var3, float var4, float var5, float var6, int var7);

    public float x();

    public float y();

    public int color();

    public int shadowColor();

    public float shadowOffset();

    default public float getWidth() {
        return 8.0f;
    }

    default public float getHeight() {
        return 8.0f;
    }

    default public float getAscent() {
        return 8.0f;
    }

    default public float getEffectiveMinX() {
        return this.x();
    }

    default public float getEffectiveMaxX() {
        return this.getEffectiveMinX() + this.getWidth();
    }

    default public float getEffectiveMinY() {
        return this.y() + 7.0f - this.getAscent();
    }

    default public float getEffectiveMaxY() {
        return this.getTop() + this.getHeight();
    }
}

