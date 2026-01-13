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
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.text.Style;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EmptyGlyph
implements Glyph {
    final GlyphMetrics glyph;

    public EmptyGlyph(float advance) {
        this.glyph = GlyphMetrics.empty(advance);
    }

    @Override
    public GlyphMetrics getMetrics() {
        return this.glyph;
    }

    @Override
    public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
        return new BakedGlyph(){

            @Override
            public GlyphMetrics getMetrics() {
                return EmptyGlyph.this.glyph;
            }

            @Override
            public  @Nullable TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
                return null;
            }
        };
    }
}
