/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.client.font.GlyphProvider
 *  net.minecraft.client.font.PlayerHeadGlyphs
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.text.StyleSpriteSource$Player
 */
package net.minecraft.client.font;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.text.StyleSpriteSource;

@Environment(value=EnvType.CLIENT)
public class PlayerHeadGlyphs {
    static final GlyphMetrics EMPTY_SPRITE_METRICS = GlyphMetrics.empty((float)8.0f);
    final PlayerSkinCache playerSkinCache;
    private final LoadingCache<StyleSpriteSource.Player, GlyphProvider> fetchingCache = CacheBuilder.newBuilder().expireAfterAccess(PlayerSkinCache.TIME_TO_LIVE).build((CacheLoader)new /* Unavailable Anonymous Inner Class!! */);

    public PlayerHeadGlyphs(PlayerSkinCache playerSkinCache) {
        this.playerSkinCache = playerSkinCache;
    }

    public GlyphProvider get(StyleSpriteSource.Player source) {
        return (GlyphProvider)this.fetchingCache.getUnchecked((Object)source);
    }
}

