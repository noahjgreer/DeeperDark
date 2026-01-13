/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.EmptyGlyph
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.SpaceFont
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpaceFont
implements Font {
    private final Int2ObjectMap<EmptyGlyph> codePointsToGlyphs;

    public SpaceFont(Map<Integer, Float> codePointsToAdvances) {
        this.codePointsToGlyphs = new Int2ObjectOpenHashMap(codePointsToAdvances.size());
        codePointsToAdvances.forEach((codePoint, glyph) -> this.codePointsToGlyphs.put(codePoint.intValue(), (Object)new EmptyGlyph(glyph.floatValue())));
    }

    public @Nullable Glyph getGlyph(int codePoint) {
        return (Glyph)this.codePointsToGlyphs.get(codePoint);
    }

    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.codePointsToGlyphs.keySet());
    }
}

