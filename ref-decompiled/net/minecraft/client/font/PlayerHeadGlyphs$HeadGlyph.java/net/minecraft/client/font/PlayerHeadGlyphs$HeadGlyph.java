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
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.DrawnSpriteGlyph;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
record PlayerHeadGlyphs.HeadGlyph(Supplier<PlayerSkinCache.Entry> skin, boolean hat, float x, float y, int color, int shadowColor, float shadowOffset, Style style) implements DrawnSpriteGlyph
{
    @Override
    public void draw(Matrix4f matrix, VertexConsumer vertexConsumer, int light, float x, float y, float z, int color) {
        float f = x + this.getEffectiveMinX();
        float g = x + this.getEffectiveMaxX();
        float h = y + this.getEffectiveMinY();
        float i = y + this.getEffectiveMaxY();
        PlayerHeadGlyphs.HeadGlyph.drawInternal(matrix, vertexConsumer, light, f, g, h, i, z, color, 8.0f, 8.0f, 8, 8, 64, 64);
        if (this.hat) {
            PlayerHeadGlyphs.HeadGlyph.drawInternal(matrix, vertexConsumer, light, f, g, h, i, z, color, 40.0f, 8.0f, 8, 8, 64, 64);
        }
    }

    private static void drawInternal(Matrix4f matrix, VertexConsumer vertexConsumer, int light, float xMin, float xMax, float yMin, float yMax, float z, int color, float regionTop, float regionLeft, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        float f = (regionTop + 0.0f) / (float)textureWidth;
        float g = (regionTop + (float)regionWidth) / (float)textureWidth;
        float h = (regionLeft + 0.0f) / (float)textureHeight;
        float i = (regionLeft + (float)regionHeight) / (float)textureHeight;
        vertexConsumer.vertex((Matrix4fc)matrix, xMin, yMin, z).texture(f, h).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, xMin, yMax, z).texture(f, i).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, xMax, yMax, z).texture(g, i).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, xMax, yMin, z).texture(g, h).color(color).light(light);
    }

    @Override
    public RenderLayer getRenderLayer(TextRenderer.TextLayerType type) {
        return this.skin.get().getTextRenderLayers().getRenderLayer(type);
    }

    @Override
    public RenderPipeline getPipeline() {
        return this.skin.get().getTextRenderLayers().guiPipeline();
    }

    @Override
    public GpuTextureView textureView() {
        return this.skin.get().getTextureView();
    }
}
