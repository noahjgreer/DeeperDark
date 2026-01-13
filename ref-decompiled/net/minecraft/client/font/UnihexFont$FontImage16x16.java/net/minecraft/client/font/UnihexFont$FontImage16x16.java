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
record UnihexFont.FontImage16x16(short[] contents) implements UnihexFont.BitmapGlyph
{
    @Override
    public int getPixels(int y) {
        return this.contents[y] << 16;
    }

    static UnihexFont.BitmapGlyph read(int lineNum, ByteList data) {
        short[] ss = new short[16];
        int i = 0;
        for (int j = 0; j < 16; ++j) {
            short s;
            int k = UnihexFont.getHexDigitValue(lineNum, data, i++);
            int l = UnihexFont.getHexDigitValue(lineNum, data, i++);
            int m = UnihexFont.getHexDigitValue(lineNum, data, i++);
            int n = UnihexFont.getHexDigitValue(lineNum, data, i++);
            ss[j] = s = (short)(k << 12 | l << 8 | m << 4 | n);
        }
        return new UnihexFont.FontImage16x16(ss);
    }

    @Override
    public int bitWidth() {
        return 16;
    }
}
