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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UploadableGlyph;

@Environment(value=EnvType.CLIENT)
class BitmapFont.BitmapFontGlyph.1
implements UploadableGlyph {
    BitmapFont.BitmapFontGlyph.1() {
    }

    @Override
    public float getOversample() {
        return 1.0f / BitmapFontGlyph.this.scaleFactor;
    }

    @Override
    public int getWidth() {
        return BitmapFontGlyph.this.width;
    }

    @Override
    public int getHeight() {
        return BitmapFontGlyph.this.height;
    }

    @Override
    public float getAscent() {
        return BitmapFontGlyph.this.ascent;
    }

    @Override
    public void upload(int x, int y, GpuTexture texture) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, BitmapFontGlyph.this.image, 0, 0, x, y, BitmapFontGlyph.this.width, BitmapFontGlyph.this.height, BitmapFontGlyph.this.x, BitmapFontGlyph.this.y);
    }

    @Override
    public boolean hasColor() {
        return BitmapFontGlyph.this.image.getFormat().getChannelCount() > 1;
    }
}
