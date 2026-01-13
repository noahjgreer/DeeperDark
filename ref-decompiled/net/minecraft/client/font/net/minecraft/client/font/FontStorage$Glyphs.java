/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class FontStorage.Glyphs
implements GlyphProvider {
    private final boolean advanceValidating;

    public FontStorage.Glyphs(boolean advanceValidating) {
        this.advanceValidating = advanceValidating;
    }

    @Override
    public BakedGlyph get(int codePoint) {
        return FontStorage.this.getBaked(codePoint).get(this.advanceValidating).get();
    }

    @Override
    public BakedGlyph getObfuscated(Random random, int width) {
        return FontStorage.this.getObfuscatedBakedGlyph(random, width);
    }
}
