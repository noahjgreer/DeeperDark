/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.GlyphProvider
 *  net.minecraft.client.texture.FixedGlyphProvider
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public record FixedGlyphProvider(BakedGlyph glyph) implements GlyphProvider
{
    private final BakedGlyph glyph;

    public FixedGlyphProvider(BakedGlyph glyph) {
        this.glyph = glyph;
    }

    public BakedGlyph get(int codePoint) {
        return this.glyph;
    }

    public BakedGlyph getObfuscated(Random random, int width) {
        return this.glyph;
    }

    public BakedGlyph glyph() {
        return this.glyph;
    }
}

