/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.util.freetype.FT_Face
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.util.freetype.FT_Face;

@Environment(value=EnvType.CLIENT)
class TrueTypeFont.TtfGlyph.1
implements UploadableGlyph {
    TrueTypeFont.TtfGlyph.1() {
    }

    @Override
    public int getWidth() {
        return TtfGlyph.this.width;
    }

    @Override
    public int getHeight() {
        return TtfGlyph.this.height;
    }

    @Override
    public float getOversample() {
        return TtfGlyph.this.field_2336.oversample;
    }

    @Override
    public float getBearingX() {
        return TtfGlyph.this.bearingX;
    }

    @Override
    public float getAscent() {
        return TtfGlyph.this.ascent;
    }

    @Override
    public void upload(int x, int y, GpuTexture texture) {
        FT_Face fT_Face = TtfGlyph.this.field_2336.getInfo();
        try (NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);){
            if (nativeImage.makeGlyphBitmapSubpixel(fT_Face, TtfGlyph.this.glyphIndex)) {
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, nativeImage, 0, 0, x, y, TtfGlyph.this.width, TtfGlyph.this.height, 0, 0);
            }
        }
    }

    @Override
    public boolean hasColor() {
        return false;
    }
}
