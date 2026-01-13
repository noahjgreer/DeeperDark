/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BlankFont
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Glyph
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlankFont
implements Font {
    private static final Glyph MISSING = new /* Unavailable Anonymous Inner Class!! */;

    public @Nullable Glyph getGlyph(int codePoint) {
        return MISSING;
    }

    public IntSet getProvidedGlyphs() {
        return IntSets.EMPTY_SET;
    }
}

