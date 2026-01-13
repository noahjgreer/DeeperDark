/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyphImpl;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
static final class BakedGlyphImpl.BakedGlyphRect
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

    BakedGlyphImpl.BakedGlyphRect(float x, float y, int color, int shadowColor, BakedGlyphImpl glyph, Style style, float boldOffset, float shadowOffset) {
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
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedGlyphImpl.BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedGlyphImpl.BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedGlyphImpl.BakedGlyphRect.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this, object);
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
