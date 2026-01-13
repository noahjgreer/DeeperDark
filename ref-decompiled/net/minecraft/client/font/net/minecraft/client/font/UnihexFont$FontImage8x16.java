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
record UnihexFont.FontImage8x16(byte[] contents) implements UnihexFont.BitmapGlyph
{
    @Override
    public int getPixels(int y) {
        return this.contents[y] << 24;
    }

    static UnihexFont.BitmapGlyph read(int lineNum, ByteList data) {
        byte[] bs = new byte[16];
        int i = 0;
        for (int j = 0; j < 16; ++j) {
            byte b;
            int k = UnihexFont.getHexDigitValue(lineNum, data, i++);
            int l = UnihexFont.getHexDigitValue(lineNum, data, i++);
            bs[j] = b = (byte)(k << 4 | l);
        }
        return new UnihexFont.FontImage8x16(bs);
    }

    @Override
    public int bitWidth() {
        return 8;
    }
}
