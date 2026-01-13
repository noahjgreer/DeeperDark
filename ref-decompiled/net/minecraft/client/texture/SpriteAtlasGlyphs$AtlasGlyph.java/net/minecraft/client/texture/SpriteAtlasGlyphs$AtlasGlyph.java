/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.DrawnSpriteGlyph;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
record SpriteAtlasGlyphs.AtlasGlyph(TextRenderLayerSet renderTypes, GpuTextureView textureView, Sprite sprite, float x, float y, int color, int shadowColor, float shadowOffset, Style style) implements DrawnSpriteGlyph
{
    @Override
    public void draw(Matrix4f matrix, VertexConsumer vertexConsumer, int light, float x, float y, float z, int color) {
        float f = x + this.getEffectiveMinX();
        float g = x + this.getEffectiveMaxX();
        float h = y + this.getEffectiveMinY();
        float i = y + this.getEffectiveMaxY();
        vertexConsumer.vertex((Matrix4fc)matrix, f, h, z).texture(this.sprite.getMinU(), this.sprite.getMinV()).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, f, i, z).texture(this.sprite.getMinU(), this.sprite.getMaxV()).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, g, i, z).texture(this.sprite.getMaxU(), this.sprite.getMaxV()).color(color).light(light);
        vertexConsumer.vertex((Matrix4fc)matrix, g, h, z).texture(this.sprite.getMaxU(), this.sprite.getMinV()).color(color).light(light);
    }

    @Override
    public RenderLayer getRenderLayer(TextRenderer.TextLayerType type) {
        return this.renderTypes.getRenderLayer(type);
    }

    @Override
    public RenderPipeline getPipeline() {
        return this.renderTypes.guiPipeline();
    }
}
