/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.BakedGlyphImpl
 *  net.minecraft.client.font.BakedGlyphImpl$BakedGlyphRect
 *  net.minecraft.client.font.BakedGlyphImpl$Rectangle
 *  net.minecraft.client.font.EffectGlyph
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.client.font.TextDrawable
 *  net.minecraft.client.font.TextDrawable$DrawnGlyphRect
 *  net.minecraft.client.font.TextRenderLayerSet
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.text.Style
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.BakedGlyphImpl;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BakedGlyphImpl
implements BakedGlyph,
EffectGlyph {
    public static final float Z_OFFSET = 0.001f;
    final GlyphMetrics glyph;
    final TextRenderLayerSet textRenderLayers;
    final GpuTextureView textureView;
    private final float minU;
    private final float maxU;
    private final float minV;
    private final float maxV;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;

    public BakedGlyphImpl(GlyphMetrics glyph, TextRenderLayerSet textRenderLayers, GpuTextureView textureView, float minU, float maxU, float minV, float maxV, float minX, float maxX, float minY, float maxY) {
        this.glyph = glyph;
        this.textRenderLayers = textRenderLayers;
        this.textureView = textureView;
        this.minU = minU;
        this.maxU = maxU;
        this.minV = minV;
        this.maxV = maxV;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    float getEffectiveMinX(BakedGlyphRect glyph) {
        return glyph.x + this.minX + (glyph.style.isItalic() ? Math.min(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) - BakedGlyphImpl.getXExpansion((boolean)glyph.style.isBold());
    }

    float getEffectiveMinY(BakedGlyphRect glyph) {
        return glyph.y + this.minY - BakedGlyphImpl.getXExpansion((boolean)glyph.style.isBold());
    }

    float getEffectiveMaxX(BakedGlyphRect glyph) {
        return glyph.x + this.maxX + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + (glyph.style.isItalic() ? Math.max(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) + BakedGlyphImpl.getXExpansion((boolean)glyph.style.isBold());
    }

    float getEffectiveMaxY(BakedGlyphRect glyph) {
        return glyph.y + this.maxY + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + BakedGlyphImpl.getXExpansion((boolean)glyph.style.isBold());
    }

    void draw(BakedGlyphRect glyph, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
        float k;
        float h;
        Style style = glyph.style();
        boolean bl = style.isItalic();
        float f = glyph.x();
        float g = glyph.y();
        int i = glyph.color();
        boolean bl2 = style.isBold();
        float f2 = h = fixedZ ? 0.0f : 0.001f;
        if (glyph.hasShadow()) {
            int j = glyph.shadowColor();
            this.draw(bl, f + glyph.shadowOffset(), g + glyph.shadowOffset(), 0.0f, matrix, vertexConsumer, j, bl2, light);
            if (bl2) {
                this.draw(bl, f + glyph.boldOffset() + glyph.shadowOffset(), g + glyph.shadowOffset(), h, matrix, vertexConsumer, j, true, light);
            }
            k = fixedZ ? 0.0f : 0.03f;
        } else {
            k = 0.0f;
        }
        this.draw(bl, f, g, k, matrix, vertexConsumer, i, bl2, light);
        if (bl2) {
            this.draw(bl, f + glyph.boldOffset(), g, k + h, matrix, vertexConsumer, i, true, light);
        }
    }

    private void draw(boolean italic, float x, float y, float z, Matrix4f matrix, VertexConsumer vertexConsumer, int color, boolean bold, int light) {
        float f = x + this.minX;
        float g = x + this.maxX;
        float h = y + this.minY;
        float i = y + this.maxY;
        float j = italic ? this.getItalicOffsetAtMinY() : 0.0f;
        float k = italic ? this.getItalicOffsetAtMaxY() : 0.0f;
        float l = BakedGlyphImpl.getXExpansion((boolean)bold);
        vertexConsumer.vertex((Matrix4fc)matrix, f + j - l, h - l, z).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, f + k - l, i + l, z).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, g + k + l, i + l, z).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, g + j + l, h - l, z).color(color).texture(this.maxU, this.minV).light(light);
    }

    private static float getXExpansion(boolean bold) {
        return bold ? 0.1f : 0.0f;
    }

    private float getItalicOffsetAtMaxY() {
        return 1.0f - 0.25f * this.maxY;
    }

    private float getItalicOffsetAtMinY() {
        return 1.0f - 0.25f * this.minY;
    }

    void drawRectangle(Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
        float f;
        float f2 = f = fixedZ ? 0.0f : rectangle.zIndex;
        if (rectangle.hasShadow()) {
            this.drawRectangle(rectangle, rectangle.shadowOffset(), f, rectangle.shadowColor(), vertexConsumer, light, matrix);
            f += fixedZ ? 0.0f : 0.03f;
        }
        this.drawRectangle(rectangle, 0.0f, f, rectangle.color, vertexConsumer, light, matrix);
    }

    private void drawRectangle(Rectangle rectangle, float shadowOffset, float zOffset, int color, VertexConsumer vertexConsumer, int light, Matrix4f matrix) {
        vertexConsumer.vertex((Matrix4fc)matrix, rectangle.minX + shadowOffset, rectangle.maxY + shadowOffset, zOffset).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, rectangle.maxX + shadowOffset, rectangle.maxY + shadowOffset, zOffset).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, rectangle.maxX + shadowOffset, rectangle.minY + shadowOffset, zOffset).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, rectangle.minX + shadowOffset, rectangle.minY + shadowOffset, zOffset).color(color).texture(this.maxU, this.minV).light(light);
    }

    public GlyphMetrics getMetrics() {
        return this.glyph;
    }

    public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
        return new BakedGlyphRect(x, y, color, shadowColor, this, style, boldOffset, shadowOffset);
    }

    public TextDrawable create(float minX, float minY, float maxX, float maxY, float depth, int color, int shadowColor, float shadowOffset) {
        return new Rectangle(this, minX, minY, maxX, maxY, depth, color, shadowColor, shadowOffset);
    }
}

