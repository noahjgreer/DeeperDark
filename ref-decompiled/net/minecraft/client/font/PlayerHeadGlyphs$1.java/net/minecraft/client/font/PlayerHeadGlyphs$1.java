/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.cache.CacheLoader;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.PlayerHeadGlyphs;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.texture.FixedGlyphProvider;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;

@Environment(value=EnvType.CLIENT)
class PlayerHeadGlyphs.1
extends CacheLoader<StyleSpriteSource.Player, GlyphProvider> {
    PlayerHeadGlyphs.1() {
    }

    public GlyphProvider load(StyleSpriteSource.Player player) {
        final Supplier<PlayerSkinCache.Entry> supplier = PlayerHeadGlyphs.this.playerSkinCache.getSupplier(player.profile());
        final boolean bl = player.hat();
        return new FixedGlyphProvider(new BakedGlyph(){

            @Override
            public GlyphMetrics getMetrics() {
                return EMPTY_SPRITE_METRICS;
            }

            @Override
            public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
                return new PlayerHeadGlyphs.HeadGlyph(supplier, bl, x, y, color, shadowColor, shadowOffset, style);
            }
        });
    }

    public /* synthetic */ Object load(Object source) throws Exception {
        return this.load((StyleSpriteSource.Player)source);
    }
}
