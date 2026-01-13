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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.DrawnSpriteGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.FixedGlyphProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasGlyphs {
    static final GlyphMetrics EMPTY_SPRITE_METRICS = GlyphMetrics.empty(8.0f);
    final SpriteAtlasTexture atlasTexture;
    final TextRenderLayerSet renderLayerSet;
    private final GlyphProvider missingGlyphProvider;
    private final Map<Identifier, GlyphProvider> cachedGlyphs = new HashMap<Identifier, GlyphProvider>();
    private final Function<Identifier, GlyphProvider> computeSprite;

    public SpriteAtlasGlyphs(SpriteAtlasTexture atlasTexture) {
        this.atlasTexture = atlasTexture;
        this.renderLayerSet = TextRenderLayerSet.of(atlasTexture.getId());
        Sprite sprite = atlasTexture.getMissingSprite();
        this.missingGlyphProvider = this.createFixedGlyphProvider(sprite);
        this.computeSprite = id -> {
            Sprite sprite2 = atlasTexture.getSprite((Identifier)id);
            if (sprite2 == sprite) {
                return this.missingGlyphProvider;
            }
            return this.createFixedGlyphProvider(sprite2);
        };
    }

    public GlyphProvider getGlyphProvider(Identifier id) {
        return this.cachedGlyphs.computeIfAbsent(id, this.computeSprite);
    }

    private GlyphProvider createFixedGlyphProvider(final Sprite sprite) {
        return new FixedGlyphProvider(new BakedGlyph(){

            @Override
            public GlyphMetrics getMetrics() {
                return EMPTY_SPRITE_METRICS;
            }

            @Override
            public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
                return new AtlasGlyph(SpriteAtlasGlyphs.this.renderLayerSet, SpriteAtlasGlyphs.this.atlasTexture.getGlTextureView(), sprite, x, y, color, shadowColor, shadowOffset, style);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    record AtlasGlyph(TextRenderLayerSet renderTypes, GpuTextureView textureView, Sprite sprite, float x, float y, int color, int shadowColor, float shadowOffset, Style style) implements DrawnSpriteGlyph
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
}
