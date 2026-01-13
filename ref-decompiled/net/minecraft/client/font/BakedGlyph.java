/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.text.Style
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.text.Style;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BakedGlyph {
    public GlyphMetrics getMetrics();

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable TextDrawable.DrawnGlyphRect create(float var1, float var2, int var3, int var4, Style var5, float var6, float var7);
}

