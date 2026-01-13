/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.bytes.ByteList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.UnihexFont;

@Environment(value=EnvType.CLIENT)
record UnihexFont.FontImage32x16(int[] contents, int bitWidth) implements UnihexFont.BitmapGlyph
{
    private static final int field_44775 = 24;

    @Override
    public int getPixels(int y) {
        return this.contents[y];
    }

    static UnihexFont.BitmapGlyph read24x16(int lineNum, ByteList data) {
        int[] is = new int[16];
        int i = 0;
        int j = 0;
        for (int k = 0; k < 16; ++k) {
            int l = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int m = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int n = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int o = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int p = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int q = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int r = l << 20 | m << 16 | n << 12 | o << 8 | p << 4 | q;
            is[k] = r << 8;
            i |= r;
        }
        return new UnihexFont.FontImage32x16(is, 24);
    }

    public static UnihexFont.BitmapGlyph read32x16(int lineNum, ByteList data) {
        int[] is = new int[16];
        int i = 0;
        int j = 0;
        for (int k = 0; k < 16; ++k) {
            int t;
            int l = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int m = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int n = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int o = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int p = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int q = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int r = UnihexFont.getHexDigitValue(lineNum, data, j++);
            int s = UnihexFont.getHexDigitValue(lineNum, data, j++);
            is[k] = t = l << 28 | m << 24 | n << 20 | o << 16 | p << 12 | q << 8 | r << 4 | s;
            i |= t;
        }
        return new UnihexFont.FontImage32x16(is, 32);
    }
}
