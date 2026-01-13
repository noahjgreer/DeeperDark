/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.text.Style;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class EmptyGlyph.1
implements BakedGlyph {
    EmptyGlyph.1() {
    }

    @Override
    public GlyphMetrics getMetrics() {
        return EmptyGlyph.this.glyph;
    }

    @Override
    public  @Nullable TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
        return null;
    }
}
