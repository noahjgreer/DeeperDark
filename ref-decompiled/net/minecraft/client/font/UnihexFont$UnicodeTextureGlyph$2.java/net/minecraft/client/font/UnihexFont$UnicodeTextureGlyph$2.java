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
import java.nio.Buffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UnihexFont;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
class UnihexFont.UnicodeTextureGlyph.2
implements UploadableGlyph {
    UnihexFont.UnicodeTextureGlyph.2() {
    }

    @Override
    public float getOversample() {
        return 2.0f;
    }

    @Override
    public int getWidth() {
        return UnicodeTextureGlyph.this.width();
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void upload(int x, int y, GpuTexture texture) {
        IntBuffer intBuffer = MemoryUtil.memAllocInt((int)(UnicodeTextureGlyph.this.width() * 16));
        UnihexFont.addGlyphPixels(intBuffer, UnicodeTextureGlyph.this.contents, UnicodeTextureGlyph.this.left, UnicodeTextureGlyph.this.right);
        intBuffer.rewind();
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, MemoryUtil.memByteBuffer((IntBuffer)intBuffer), NativeImage.Format.RGBA, 0, 0, x, y, UnicodeTextureGlyph.this.width(), 16);
        MemoryUtil.memFree((Buffer)intBuffer);
    }

    @Override
    public boolean hasColor() {
        return true;
    }
}
