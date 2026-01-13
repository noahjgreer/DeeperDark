/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.math.ColorHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class OverlayTexture
implements AutoCloseable {
    private static final int field_32956 = 16;
    public static final int field_32953 = 0;
    public static final int field_32954 = 3;
    public static final int field_32955 = 10;
    public static final int DEFAULT_UV = OverlayTexture.packUv((int)0, (int)10);
    private final NativeImageBackedTexture texture = new NativeImageBackedTexture("Entity Color Overlay", 16, 16, false);

    public OverlayTexture() {
        NativeImage nativeImage = this.texture.getImage();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    nativeImage.setColorArgb(j, i, -1291911168);
                    continue;
                }
                int k = (int)((1.0f - (float)j / 15.0f * 0.75f) * 255.0f);
                nativeImage.setColorArgb(j, i, ColorHelper.whiteWithAlpha((int)k));
            }
        }
        this.texture.upload();
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public static int getU(float whiteOverlayProgress) {
        return (int)(whiteOverlayProgress * 15.0f);
    }

    public static int getV(boolean hurt) {
        return hurt ? 3 : 10;
    }

    public static int packUv(int u, int v) {
        return u | v << 16;
    }

    public static int getUv(float whiteOverlayProgress, boolean hurt) {
        return OverlayTexture.packUv((int)OverlayTexture.getU((float)whiteOverlayProgress), (int)OverlayTexture.getV((boolean)hurt));
    }

    public GpuTextureView getTextureView() {
        return this.texture.getGlTextureView();
    }
}

