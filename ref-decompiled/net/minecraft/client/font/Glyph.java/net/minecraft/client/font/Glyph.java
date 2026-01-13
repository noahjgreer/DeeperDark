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
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;

@Environment(value=EnvType.CLIENT)
public interface Glyph {
    public GlyphMetrics getMetrics();

    public BakedGlyph bake(AbstractGlyphBaker var1);

    @Environment(value=EnvType.CLIENT)
    public static interface AbstractGlyphBaker {
        public BakedGlyph bake(GlyphMetrics var1, UploadableGlyph var2);

        public BakedGlyph getBlankGlyph();
    }
}
