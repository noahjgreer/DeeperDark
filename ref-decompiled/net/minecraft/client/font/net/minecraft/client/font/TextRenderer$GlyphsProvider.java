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
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.text.StyleSpriteSource;

@Environment(value=EnvType.CLIENT)
public static interface TextRenderer.GlyphsProvider {
    public GlyphProvider getGlyphs(StyleSpriteSource var1);

    public EffectGlyph getRectangleGlyph();
}
