/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.Glyph$AbstractGlyphBaker
 *  net.minecraft.client.font.GlyphMetrics
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;

@Environment(value=EnvType.CLIENT)
public interface Glyph {
    public GlyphMetrics getMetrics();

    public BakedGlyph bake(AbstractGlyphBaker var1);
}

