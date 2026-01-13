/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.MipmapHelper
 *  net.minecraft.client.texture.MipmapStrategy
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MipmapStrategy;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MipmapHelper {
    private static final String ITEM_PREFIX = "item/";
    private static final float DEFAULT_ALPHA_THRESHOLD = 0.5f;
    private static final float STRICT_CUTOUT_ALPHA_THRESHOLD = 0.3f;

    private MipmapHelper() {
    }

    private static float getOpacityCoverage(NativeImage image, float alphaThreshold, float alphaMulti) {
        int i = image.getWidth();
        int j = image.getHeight();
        float f = 0.0f;
        int k = 4;
        for (int l = 0; l < j - 1; ++l) {
            for (int m = 0; m < i - 1; ++m) {
                float g = Math.clamp(ColorHelper.getAlphaFloat((int)image.getColorArgb(m, l)) * alphaMulti, 0.0f, 1.0f);
                float h = Math.clamp(ColorHelper.getAlphaFloat((int)image.getColorArgb(m + 1, l)) * alphaMulti, 0.0f, 1.0f);
                float n = Math.clamp(ColorHelper.getAlphaFloat((int)image.getColorArgb(m, l + 1)) * alphaMulti, 0.0f, 1.0f);
                float o = Math.clamp(ColorHelper.getAlphaFloat((int)image.getColorArgb(m + 1, l + 1)) * alphaMulti, 0.0f, 1.0f);
                float p = 0.0f;
                for (int q = 0; q < 4; ++q) {
                    float r = ((float)q + 0.5f) / 4.0f;
                    for (int s = 0; s < 4; ++s) {
                        float t = ((float)s + 0.5f) / 4.0f;
                        float u = g * (1.0f - t) * (1.0f - r) + h * t * (1.0f - r) + n * (1.0f - t) * r + o * t * r;
                        if (!(u > alphaThreshold)) continue;
                        p += 1.0f;
                    }
                }
                f += p / 16.0f;
            }
        }
        return f / (float)((i - 1) * (j - 1));
    }

    private static void adjustAlphaForTargetCoverage(NativeImage image, float targetCoverage, float alphaThreshold, float cutoffBias) {
        int m;
        float f = 0.0f;
        float g = 4.0f;
        float h = 1.0f;
        float i = 1.0f;
        float j = Float.MAX_VALUE;
        int k = image.getWidth();
        int l = image.getHeight();
        for (m = 0; m < 5; ++m) {
            float n = MipmapHelper.getOpacityCoverage((NativeImage)image, (float)alphaThreshold, (float)h);
            float o = Math.abs(n - targetCoverage);
            if (o < j) {
                j = o;
                i = h;
            }
            if (n < targetCoverage) {
                f = h;
            } else {
                if (!(n > targetCoverage)) break;
                g = h;
            }
            h = (f + g) * 0.5f;
        }
        for (m = 0; m < l; ++m) {
            for (int p = 0; p < k; ++p) {
                int q = image.getColorArgb(p, m);
                float r = ColorHelper.getAlphaFloat((int)q);
                r = r * i + cutoffBias + 0.025f;
                r = Math.clamp(r, 0.0f, 1.0f);
                image.setColorArgb(p, m, ColorHelper.withAlpha((float)r, (int)q));
            }
        }
    }

    public static NativeImage[] getMipmapLevelsImages(Identifier id, NativeImage[] mipmapLevelImages, int mipmapLevels, MipmapStrategy strategy, float cutoffBias) {
        if (strategy == MipmapStrategy.AUTO) {
            MipmapStrategy mipmapStrategy = strategy = MipmapHelper.hasAlpha((NativeImage)mipmapLevelImages[0]) ? MipmapStrategy.CUTOUT : MipmapStrategy.MEAN;
        }
        if (mipmapLevelImages.length == 1 && !id.getPath().startsWith("item/")) {
            if (strategy == MipmapStrategy.CUTOUT || strategy == MipmapStrategy.STRICT_CUTOUT) {
                TextureUtil.solidify((NativeImage)mipmapLevelImages[0]);
            } else if (strategy == MipmapStrategy.DARK_CUTOUT) {
                TextureUtil.fillEmptyAreasWithDarkColor((NativeImage)mipmapLevelImages[0]);
            }
        }
        if (mipmapLevels + 1 <= mipmapLevelImages.length) {
            return mipmapLevelImages;
        }
        NativeImage[] nativeImages = new NativeImage[mipmapLevels + 1];
        nativeImages[0] = mipmapLevelImages[0];
        boolean bl = strategy == MipmapStrategy.CUTOUT || strategy == MipmapStrategy.STRICT_CUTOUT || strategy == MipmapStrategy.DARK_CUTOUT;
        float f = strategy == MipmapStrategy.STRICT_CUTOUT ? 0.3f : 0.5f;
        float g = bl ? MipmapHelper.getOpacityCoverage((NativeImage)mipmapLevelImages[0], (float)f, (float)1.0f) : 0.0f;
        for (int i = 1; i <= mipmapLevels; ++i) {
            if (i < mipmapLevelImages.length) {
                nativeImages[i] = mipmapLevelImages[i];
            } else {
                NativeImage nativeImage = nativeImages[i - 1];
                NativeImage nativeImage2 = new NativeImage(nativeImage.getWidth() >> 1, nativeImage.getHeight() >> 1, false);
                int j = nativeImage2.getWidth();
                int k = nativeImage2.getHeight();
                for (int l = 0; l < j; ++l) {
                    for (int m = 0; m < k; ++m) {
                        int n = nativeImage.getColorArgb(l * 2 + 0, m * 2 + 0);
                        int o = nativeImage.getColorArgb(l * 2 + 1, m * 2 + 0);
                        int p = nativeImage.getColorArgb(l * 2 + 0, m * 2 + 1);
                        int q = nativeImage.getColorArgb(l * 2 + 1, m * 2 + 1);
                        int r = strategy == MipmapStrategy.DARK_CUTOUT ? MipmapHelper.blendDarkenedCutout((int)n, (int)o, (int)p, (int)q) : ColorHelper.interpolate((int)n, (int)o, (int)p, (int)q);
                        nativeImage2.setColorArgb(l, m, r);
                    }
                }
                nativeImages[i] = nativeImage2;
            }
            if (!bl) continue;
            MipmapHelper.adjustAlphaForTargetCoverage((NativeImage)nativeImages[i], (float)g, (float)f, (float)cutoffBias);
        }
        return nativeImages;
    }

    private static boolean hasAlpha(NativeImage image) {
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                if (ColorHelper.getAlpha((int)image.getColorArgb(i, j)) != 0) continue;
                return true;
            }
        }
        return false;
    }

    private static int blendDarkenedCutout(int nw, int ne, int sw, int se) {
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        float i = 0.0f;
        if (ColorHelper.getAlpha((int)nw) != 0) {
            f += ColorHelper.srgbToLinear((int)ColorHelper.getAlpha((int)nw));
            g += ColorHelper.srgbToLinear((int)ColorHelper.getRed((int)nw));
            h += ColorHelper.srgbToLinear((int)ColorHelper.getGreen((int)nw));
            i += ColorHelper.srgbToLinear((int)ColorHelper.getBlue((int)nw));
        }
        if (ColorHelper.getAlpha((int)ne) != 0) {
            f += ColorHelper.srgbToLinear((int)ColorHelper.getAlpha((int)ne));
            g += ColorHelper.srgbToLinear((int)ColorHelper.getRed((int)ne));
            h += ColorHelper.srgbToLinear((int)ColorHelper.getGreen((int)ne));
            i += ColorHelper.srgbToLinear((int)ColorHelper.getBlue((int)ne));
        }
        if (ColorHelper.getAlpha((int)sw) != 0) {
            f += ColorHelper.srgbToLinear((int)ColorHelper.getAlpha((int)sw));
            g += ColorHelper.srgbToLinear((int)ColorHelper.getRed((int)sw));
            h += ColorHelper.srgbToLinear((int)ColorHelper.getGreen((int)sw));
            i += ColorHelper.srgbToLinear((int)ColorHelper.getBlue((int)sw));
        }
        if (ColorHelper.getAlpha((int)se) != 0) {
            f += ColorHelper.srgbToLinear((int)ColorHelper.getAlpha((int)se));
            g += ColorHelper.srgbToLinear((int)ColorHelper.getRed((int)se));
            h += ColorHelper.srgbToLinear((int)ColorHelper.getGreen((int)se));
            i += ColorHelper.srgbToLinear((int)ColorHelper.getBlue((int)se));
        }
        return ColorHelper.getArgb((int)ColorHelper.linearToSrgb((float)(f /= 4.0f)), (int)ColorHelper.linearToSrgb((float)(g /= 4.0f)), (int)ColorHelper.linearToSrgb((float)(h /= 4.0f)), (int)ColorHelper.linearToSrgb((float)(i /= 4.0f)));
    }
}

