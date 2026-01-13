/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;

@Environment(value=EnvType.CLIENT)
class FontStorage.2
implements Glyph.AbstractGlyphBaker {
    FontStorage.2() {
    }

    @Override
    public BakedGlyph bake(GlyphMetrics metrics, UploadableGlyph renderable) {
        return Objects.requireNonNullElse(FontStorage.this.glyphBaker.bake(metrics, renderable), FontStorage.this.blankBakedGlyph);
    }

    @Override
    public BakedGlyph getBlankGlyph() {
        return FontStorage.this.blankBakedGlyph;
    }
}
