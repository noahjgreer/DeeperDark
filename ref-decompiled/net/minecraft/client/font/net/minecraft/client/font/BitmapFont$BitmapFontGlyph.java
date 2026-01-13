/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;

@Environment(value=EnvType.CLIENT)
static final class BitmapFont.BitmapFontGlyph
extends Record
implements Glyph {
    final float scaleFactor;
    final NativeImage image;
    final int x;
    final int y;
    final int width;
    final int height;
    private final int advance;
    final int ascent;

    BitmapFont.BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
        this.scaleFactor = scaleFactor;
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.advance = advance;
        this.ascent = ascent;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return GlyphMetrics.empty(this.advance);
    }

    @Override
    public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
        return baker.bake(this.getMetrics(), new UploadableGlyph(){

            @Override
            public float getOversample() {
                return 1.0f / scaleFactor;
            }

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public float getAscent() {
                return ascent;
            }

            @Override
            public void upload(int x, int y, GpuTexture texture) {
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, image, 0, 0, x, y, width, height, x, y);
            }

            @Override
            public boolean hasColor() {
                return image.getFormat().getChannelCount() > 1;
            }
        });
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BitmapFont.BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BitmapFont.BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BitmapFont.BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this, object);
    }

    public float scaleFactor() {
        return this.scaleFactor;
    }

    public NativeImage image() {
        return this.image;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int advance() {
        return this.advance;
    }

    public int ascent() {
        return this.ascent;
    }
}
