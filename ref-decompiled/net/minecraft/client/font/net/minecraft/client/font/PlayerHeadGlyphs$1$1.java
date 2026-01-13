/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.PlayerHeadGlyphs;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
class PlayerHeadGlyphs.1
implements BakedGlyph {
    final /* synthetic */ Supplier field_62474;
    final /* synthetic */ boolean field_62475;

    PlayerHeadGlyphs.1() {
        this.field_62474 = supplier;
        this.field_62475 = bl;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return EMPTY_SPRITE_METRICS;
    }

    @Override
    public TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
        return new PlayerHeadGlyphs.HeadGlyph(this.field_62474, this.field_62475, x, y, color, shadowColor, shadowOffset, style);
    }
}
