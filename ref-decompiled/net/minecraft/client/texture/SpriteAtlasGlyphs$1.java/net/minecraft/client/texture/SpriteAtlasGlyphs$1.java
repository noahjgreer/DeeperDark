/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasGlyphs;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
class SpriteAtlasGlyphs.1
implements BakedGlyph {
    final /* synthetic */ Sprite field_62133;

    SpriteAtlasGlyphs.1() {
        this.field_62133 = sprite;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return EMPTY_SPRITE_METRICS;
    }

    @Override
    public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
        return new SpriteAtlasGlyphs.AtlasGlyph(SpriteAtlasGlyphs.this.renderLayerSet, SpriteAtlasGlyphs.this.atlasTexture.getGlTextureView(), this.field_62133, x, y, color, shadowColor, shadowOffset, style);
    }
}
