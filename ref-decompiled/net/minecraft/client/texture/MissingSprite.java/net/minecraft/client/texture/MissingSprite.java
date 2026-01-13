/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class MissingSprite {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final String MISSINGNO_ID = "missingno";
    private static final Identifier MISSINGNO = Identifier.ofVanilla("missingno");

    public static NativeImage createImage() {
        return MissingSprite.createImage(16, 16);
    }

    public static NativeImage createImage(int width, int height) {
        NativeImage nativeImage = new NativeImage(width, height, false);
        int i = -524040;
        for (int j = 0; j < height; ++j) {
            for (int k = 0; k < width; ++k) {
                if (j < height / 2 ^ k < width / 2) {
                    nativeImage.setColorArgb(k, j, -524040);
                    continue;
                }
                nativeImage.setColorArgb(k, j, -16777216);
            }
        }
        return nativeImage;
    }

    public static SpriteContents createSpriteContents() {
        NativeImage nativeImage = MissingSprite.createImage(16, 16);
        return new SpriteContents(MISSINGNO, new SpriteDimensions(16, 16), nativeImage);
    }

    public static Identifier getMissingSpriteId() {
        return MISSINGNO;
    }
}
