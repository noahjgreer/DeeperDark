/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BitmapFont
 *  net.minecraft.client.font.BitmapFont$BitmapFontGlyph
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.GlyphContainer
 *  net.minecraft.client.texture.NativeImage
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.texture.NativeImage;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BitmapFont
implements Font {
    static final Logger LOGGER = LogUtils.getLogger();
    private final NativeImage image;
    private final GlyphContainer<BitmapFontGlyph> glyphs;

    BitmapFont(NativeImage image, GlyphContainer<BitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public void close() {
        this.image.close();
    }

    public @Nullable Glyph getGlyph(int codePoint) {
        return (Glyph)this.glyphs.get(codePoint);
    }

    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.getProvidedGlyphs());
    }
}

