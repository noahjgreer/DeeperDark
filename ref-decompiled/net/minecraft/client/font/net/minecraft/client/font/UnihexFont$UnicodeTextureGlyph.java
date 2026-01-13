/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UnihexFont;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
static final class UnihexFont.UnicodeTextureGlyph
extends Record
implements Glyph {
    final UnihexFont.BitmapGlyph contents;
    final int left;
    final int right;

    UnihexFont.UnicodeTextureGlyph(UnihexFont.BitmapGlyph contents, int left, int right) {
        this.contents = contents;
        this.left = left;
        this.right = right;
    }

    public int width() {
        return this.right - this.left + 1;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return new GlyphMetrics(){

            @Override
            public float getAdvance() {
                return this.width() / 2 + 1;
            }

            @Override
            public float getShadowOffset() {
                return 0.5f;
            }

            @Override
            public float getBoldOffset() {
                return 0.5f;
            }
        };
    }

    @Override
    public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
        return baker.bake(this.getMetrics(), new UploadableGlyph(){

            @Override
            public float getOversample() {
                return 2.0f;
            }

            @Override
            public int getWidth() {
                return this.width();
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void upload(int x, int y, GpuTexture texture) {
                IntBuffer intBuffer = MemoryUtil.memAllocInt((int)(this.width() * 16));
                UnihexFont.addGlyphPixels(intBuffer, contents, left, right);
                intBuffer.rewind();
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, MemoryUtil.memByteBuffer((IntBuffer)intBuffer), NativeImage.Format.RGBA, 0, 0, x, y, this.width(), 16);
                MemoryUtil.memFree((Buffer)intBuffer);
            }

            @Override
            public boolean hasColor() {
                return true;
            }
        });
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnihexFont.UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnihexFont.UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnihexFont.UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this, object);
    }

    public UnihexFont.BitmapGlyph contents() {
        return this.contents;
    }

    public int left() {
        return this.left;
    }

    public int right() {
        return this.right;
    }
}
