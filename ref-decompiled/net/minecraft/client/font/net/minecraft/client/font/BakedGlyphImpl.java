/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

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
        return glyph.x + this.minX + (glyph.style.isItalic() ? Math.min(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) - BakedGlyphImpl.getXExpansion(glyph.style.isBold());
    }

    float getEffectiveMinY(BakedGlyphRect glyph) {
        return glyph.y + this.minY - BakedGlyphImpl.getXExpansion(glyph.style.isBold());
    }

    float getEffectiveMaxX(BakedGlyphRect glyph) {
        return glyph.x + this.maxX + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + (glyph.style.isItalic() ? Math.max(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) + BakedGlyphImpl.getXExpansion(glyph.style.isBold());
    }

    float getEffectiveMaxY(BakedGlyphRect glyph) {
        return glyph.y + this.maxY + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + BakedGlyphImpl.getXExpansion(glyph.style.isBold());
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
        float l = BakedGlyphImpl.getXExpansion(bold);
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

    @Override
    public GlyphMetrics getMetrics() {
        return this.glyph;
    }

    @Override
    public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
        return new BakedGlyphRect(x, y, color, shadowColor, this, style, boldOffset, shadowOffset);
    }

    @Override
    public TextDrawable create(float minX, float minY, float maxX, float maxY, float depth, int color, int shadowColor, float shadowOffset) {
        return new Rectangle(this, minX, minY, maxX, maxY, depth, color, shadowColor, shadowOffset);
    }

    @Environment(value=EnvType.CLIENT)
    static final class BakedGlyphRect
    extends Record
    implements TextDrawable.DrawnGlyphRect {
        final float x;
        final float y;
        private final int color;
        private final int shadowColor;
        private final BakedGlyphImpl glyph;
        final Style style;
        private final float boldOffset;
        final float shadowOffset;

        BakedGlyphRect(float x, float y, int color, int shadowColor, BakedGlyphImpl glyph, Style style, float boldOffset, float shadowOffset) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.shadowColor = shadowColor;
            this.glyph = glyph;
            this.style = style;
            this.boldOffset = boldOffset;
            this.shadowOffset = shadowOffset;
        }

        @Override
        public float getEffectiveMinX() {
            return this.glyph.getEffectiveMinX(this);
        }

        @Override
        public float getEffectiveMinY() {
            return this.glyph.getEffectiveMinY(this);
        }

        @Override
        public float getEffectiveMaxX() {
            return this.glyph.getEffectiveMaxX(this);
        }

        @Override
        public float getRight() {
            return this.x + this.glyph.glyph.getAdvance(this.style.isBold());
        }

        @Override
        public float getEffectiveMaxY() {
            return this.glyph.getEffectiveMaxY(this);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        @Override
        public void render(Matrix4f matrix4f, VertexConsumer consumer, int light, boolean noDepth) {
            this.glyph.draw(this, matrix4f, consumer, light, noDepth);
        }

        @Override
        public RenderLayer getRenderLayer(TextRenderer.TextLayerType type) {
            return this.glyph.textRenderLayers.getRenderLayer(type);
        }

        @Override
        public GpuTextureView textureView() {
            return this.glyph.textureView;
        }

        @Override
        public RenderPipeline getPipeline() {
            return this.glyph.textRenderLayers.guiPipeline();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this, object);
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public BakedGlyphImpl glyph() {
            return this.glyph;
        }

        @Override
        public Style style() {
            return this.style;
        }

        public float boldOffset() {
            return this.boldOffset;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Rectangle
    extends Record
    implements TextDrawable {
        private final BakedGlyphImpl glyph;
        final float minX;
        final float minY;
        final float maxX;
        final float maxY;
        final float zIndex;
        final int color;
        private final int shadowColor;
        private final float shadowOffset;

        Rectangle(BakedGlyphImpl glyph, float minX, float minY, float maxX, float maxY, float zIndex, int color, int shadowColor, float shadowOffset) {
            this.glyph = glyph;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.zIndex = zIndex;
            this.color = color;
            this.shadowColor = shadowColor;
            this.shadowOffset = shadowOffset;
        }

        @Override
        public float getEffectiveMinX() {
            return this.minX;
        }

        @Override
        public float getEffectiveMinY() {
            return this.minY;
        }

        @Override
        public float getEffectiveMaxX() {
            return this.maxX + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        @Override
        public float getEffectiveMaxY() {
            return this.maxY + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        @Override
        public void render(Matrix4f matrix4f, VertexConsumer consumer, int light, boolean noDepth) {
            this.glyph.drawRectangle(this, matrix4f, consumer, light, false);
        }

        @Override
        public RenderLayer getRenderLayer(TextRenderer.TextLayerType type) {
            return this.glyph.textRenderLayers.getRenderLayer(type);
        }

        @Override
        public GpuTextureView textureView() {
            return this.glyph.textureView;
        }

        @Override
        public RenderPipeline getPipeline() {
            return this.glyph.textRenderLayers.guiPipeline();
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this, object);
        }

        public BakedGlyphImpl glyph() {
            return this.glyph;
        }

        public float minX() {
            return this.minX;
        }

        public float minY() {
            return this.minY;
        }

        public float maxX() {
            return this.maxX;
        }

        public float maxY() {
            return this.maxY;
        }

        public float zIndex() {
            return this.zIndex;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }
}
