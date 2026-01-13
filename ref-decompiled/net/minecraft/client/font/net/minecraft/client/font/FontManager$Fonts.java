/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StyleSpriteSource;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class FontManager.Fonts
implements TextRenderer.GlyphsProvider,
AutoCloseable {
    private final boolean advanceValidating;
    private volatile @Nullable Cached cached;
    private volatile @Nullable EffectGlyph rectangle;

    FontManager.Fonts(boolean advanceValidating) {
        this.advanceValidating = advanceValidating;
    }

    public void clear() {
        this.cached = null;
        this.rectangle = null;
    }

    @Override
    public void close() {
        this.clear();
    }

    private GlyphProvider getGlyphsImpl(StyleSpriteSource source) {
        StyleSpriteSource styleSpriteSource = source;
        Objects.requireNonNull(styleSpriteSource);
        StyleSpriteSource styleSpriteSource2 = styleSpriteSource;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{StyleSpriteSource.Font.class, StyleSpriteSource.Sprite.class, StyleSpriteSource.Player.class}, (Object)styleSpriteSource2, n)) {
            case 0 -> {
                StyleSpriteSource.Font font = (StyleSpriteSource.Font)styleSpriteSource2;
                yield FontManager.this.getStorageInternal(font.id()).getGlyphs(this.advanceValidating);
            }
            case 1 -> {
                StyleSpriteSource.Sprite sprite = (StyleSpriteSource.Sprite)styleSpriteSource2;
                yield FontManager.this.getSpriteGlyphs(sprite);
            }
            case 2 -> {
                StyleSpriteSource.Player player = (StyleSpriteSource.Player)styleSpriteSource2;
                yield FontManager.this.playerHeadGlyphs.get(player);
            }
            default -> FontManager.this.missingStorage.getGlyphs(this.advanceValidating);
        };
    }

    @Override
    public GlyphProvider getGlyphs(StyleSpriteSource source) {
        Cached cached = this.cached;
        if (cached != null && source.equals(cached.source)) {
            return cached.glyphs;
        }
        GlyphProvider glyphProvider = this.getGlyphsImpl(source);
        this.cached = new Cached(source, glyphProvider);
        return glyphProvider;
    }

    @Override
    public EffectGlyph getRectangleGlyph() {
        EffectGlyph effectGlyph = this.rectangle;
        if (effectGlyph == null) {
            this.rectangle = effectGlyph = FontManager.this.getStorageInternal(StyleSpriteSource.DEFAULT.id()).getRectangleBakedGlyph();
        }
        return effectGlyph;
    }

    @Environment(value=EnvType.CLIENT)
    static final class Cached
    extends Record {
        final StyleSpriteSource source;
        final GlyphProvider glyphs;

        Cached(StyleSpriteSource source, GlyphProvider glyphs) {
            this.source = source;
            this.glyphs = glyphs;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Cached.class, "description;source", "source", "glyphs"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Cached.class, "description;source", "source", "glyphs"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Cached.class, "description;source", "source", "glyphs"}, this, object);
        }

        public StyleSpriteSource source() {
            return this.source;
        }

        public GlyphProvider glyphs() {
            return this.glyphs;
        }
    }
}
