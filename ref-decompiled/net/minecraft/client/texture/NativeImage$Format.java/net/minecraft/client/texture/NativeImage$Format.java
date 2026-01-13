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

@Environment(value=EnvType.CLIENT)
public static final class NativeImage.Format
extends Enum<NativeImage.Format> {
    public static final /* enum */ NativeImage.Format RGBA = new NativeImage.Format(4, true, true, true, false, true, 0, 8, 16, 255, 24, true);
    public static final /* enum */ NativeImage.Format RGB = new NativeImage.Format(3, true, true, true, false, false, 0, 8, 16, 255, 255, true);
    public static final /* enum */ NativeImage.Format LUMINANCE_ALPHA = new NativeImage.Format(2, false, false, false, true, true, 255, 255, 255, 0, 8, true);
    public static final /* enum */ NativeImage.Format LUMINANCE = new NativeImage.Format(1, false, false, false, true, false, 0, 0, 0, 0, 255, true);
    final int channelCount;
    private final boolean hasRed;
    private final boolean hasGreen;
    private final boolean hasBlue;
    private final boolean hasLuminance;
    private final boolean hasAlpha;
    private final int redOffset;
    private final int greenOffset;
    private final int blueOffset;
    private final int luminanceOffset;
    private final int alphaOffset;
    private final boolean writeable;
    private static final /* synthetic */ NativeImage.Format[] field_4995;

    public static NativeImage.Format[] values() {
        return (NativeImage.Format[])field_4995.clone();
    }

    public static NativeImage.Format valueOf(String string) {
        return Enum.valueOf(NativeImage.Format.class, string);
    }

    private NativeImage.Format(int channelCount, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasLuminance, boolean hasAlpha, int redOffset, int greenOffset, int blueOffset, int luminanceOffset, int alphaOffset, boolean writeable) {
        this.channelCount = channelCount;
        this.hasRed = hasRed;
        this.hasGreen = hasGreen;
        this.hasBlue = hasBlue;
        this.hasLuminance = hasLuminance;
        this.hasAlpha = hasAlpha;
        this.redOffset = redOffset;
        this.greenOffset = greenOffset;
        this.blueOffset = blueOffset;
        this.luminanceOffset = luminanceOffset;
        this.alphaOffset = alphaOffset;
        this.writeable = writeable;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public boolean hasRed() {
        return this.hasRed;
    }

    public boolean hasGreen() {
        return this.hasGreen;
    }

    public boolean hasBlue() {
        return this.hasBlue;
    }

    public boolean hasLuminance() {
        return this.hasLuminance;
    }

    public boolean hasAlpha() {
        return this.hasAlpha;
    }

    public int getRedOffset() {
        return this.redOffset;
    }

    public int getGreenOffset() {
        return this.greenOffset;
    }

    public int getBlueOffset() {
        return this.blueOffset;
    }

    public int getLuminanceOffset() {
        return this.luminanceOffset;
    }

    public int getAlphaOffset() {
        return this.alphaOffset;
    }

    public boolean hasRedChannel() {
        return this.hasLuminance || this.hasRed;
    }

    public boolean hasGreenChannel() {
        return this.hasLuminance || this.hasGreen;
    }

    public boolean hasBlueChannel() {
        return this.hasLuminance || this.hasBlue;
    }

    public boolean hasOpacityChannel() {
        return this.hasLuminance || this.hasAlpha;
    }

    public int getRedChannelOffset() {
        return this.hasLuminance ? this.luminanceOffset : this.redOffset;
    }

    public int getGreenChannelOffset() {
        return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
    }

    public int getBlueChannelOffset() {
        return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
    }

    public int getOpacityChannelOffset() {
        return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
    }

    public boolean isWriteable() {
        return this.writeable;
    }

    static NativeImage.Format fromChannelCount(int glFormat) {
        switch (glFormat) {
            case 1: {
                return LUMINANCE;
            }
            case 2: {
                return LUMINANCE_ALPHA;
            }
            case 3: {
                return RGB;
            }
        }
        return RGBA;
    }

    private static /* synthetic */ NativeImage.Format[] method_36811() {
        return new NativeImage.Format[]{RGBA, RGB, LUMINANCE_ALPHA, LUMINANCE};
    }

    static {
        field_4995 = NativeImage.Format.method_36811();
    }
}
