/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Font
extends AutoCloseable {
    public static final float DEFAULT_ASCENT = 7.0f;

    @Override
    default public void close() {
    }

    default public @Nullable Glyph getGlyph(int codePoint) {
        return null;
    }

    public IntSet getProvidedGlyphs();

    @Environment(value=EnvType.CLIENT)
    public record FontFilterPair(Font provider, FontFilterType.FilterMap filter) implements AutoCloseable
    {
        @Override
        public void close() {
            this.provider.close();
        }
    }
}
