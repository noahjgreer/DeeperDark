/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.text.StyleSpriteSource;

@Environment(value=EnvType.CLIENT)
static final class FontManager.Fonts.Cached
extends Record {
    final StyleSpriteSource source;
    final GlyphProvider glyphs;

    FontManager.Fonts.Cached(StyleSpriteSource source, GlyphProvider glyphs) {
        this.source = source;
        this.glyphs = glyphs;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontManager.Fonts.Cached.class, "description;source", "source", "glyphs"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontManager.Fonts.Cached.class, "description;source", "source", "glyphs"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontManager.Fonts.Cached.class, "description;source", "source", "glyphs"}, this, object);
    }

    public StyleSpriteSource source() {
        return this.source;
    }

    public GlyphProvider glyphs() {
        return this.glyphs;
    }
}
