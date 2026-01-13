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
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
static final class BakedGlyphImpl.Rectangle
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

    BakedGlyphImpl.Rectangle(BakedGlyphImpl glyph, float minX, float minY, float maxX, float maxY, float zIndex, int color, int shadowColor, float shadowOffset) {
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
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedGlyphImpl.Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedGlyphImpl.Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedGlyphImpl.Rectangle.class, "glyph;x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "glyph", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this, object);
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
