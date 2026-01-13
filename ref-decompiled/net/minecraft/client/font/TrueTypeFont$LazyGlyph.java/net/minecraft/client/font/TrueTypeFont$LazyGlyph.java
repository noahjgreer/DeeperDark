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
import net.minecraft.client.font.Glyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class TrueTypeFont.LazyGlyph {
    final int index;
    volatile @Nullable Glyph glyph;

    TrueTypeFont.LazyGlyph(int index) {
        this.index = index;
    }
}
