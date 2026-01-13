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
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;

@Environment(value=EnvType.CLIENT)
class BlankFont.1
implements Glyph {
    BlankFont.1() {
    }

    @Override
    public GlyphMetrics getMetrics() {
        return BuiltinEmptyGlyph.MISSING;
    }

    @Override
    public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
        return baker.getBlankGlyph();
    }
}
