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
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.util.freetype.FT_Face;

@Environment(value=EnvType.CLIENT)
class TrueTypeFont.TtfGlyph
implements Glyph {
    final int width;
    final int height;
    final float bearingX;
    final float ascent;
    private final GlyphMetrics metrics;
    final int glyphIndex;

    TrueTypeFont.TtfGlyph(float bearingX, float ascent, int width, int height, float advance, int glyphIndex) {
        this.width = width;
        this.height = height;
        this.metrics = GlyphMetrics.empty(advance / TrueTypeFont.this.oversample);
        this.bearingX = bearingX / TrueTypeFont.this.oversample;
        this.ascent = ascent / TrueTypeFont.this.oversample;
        this.glyphIndex = glyphIndex;
    }

    @Override
    public GlyphMetrics getMetrics() {
        return this.metrics;
    }

    @Override
    public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
        return baker.bake(this.metrics, new UploadableGlyph(){

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
                return TrueTypeFont.this.oversample;
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
                FT_Face fT_Face = TrueTypeFont.this.getInfo();
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
        });
    }
}
