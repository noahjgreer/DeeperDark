/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UnihexFont;

@Environment(value=EnvType.CLIENT)
public static interface UnihexFont.BitmapGlyph {
    public int getPixels(int var1);

    public int bitWidth();

    default public int getNonemptyColumnBitmask() {
        int i = 0;
        for (int j = 0; j < 16; ++j) {
            i |= this.getPixels(j);
        }
        return i;
    }

    default public int getPackedDimensions() {
        int l;
        int k;
        int i = this.getNonemptyColumnBitmask();
        int j = this.bitWidth();
        if (i == 0) {
            k = 0;
            l = j;
        } else {
            k = Integer.numberOfLeadingZeros(i);
            l = 32 - Integer.numberOfTrailingZeros(i) - 1;
        }
        return UnihexFont.Dimensions.pack(k, l);
    }
}
