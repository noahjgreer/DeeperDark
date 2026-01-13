/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.GlyphContainer
 *  net.minecraft.client.font.UnihexFont
 *  net.minecraft.client.font.UnihexFont$BitmapGlyph
 *  net.minecraft.client.font.UnihexFont$BitmapGlyphConsumer
 *  net.minecraft.client.font.UnihexFont$FontImage16x16
 *  net.minecraft.client.font.UnihexFont$FontImage32x16
 *  net.minecraft.client.font.UnihexFont$FontImage8x16
 *  net.minecraft.client.font.UnihexFont$UnicodeTextureGlyph
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.UnihexFont;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class UnihexFont
implements Font {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_44764 = 16;
    private static final int field_44765 = 2;
    private static final int field_44766 = 32;
    private static final int field_44767 = 64;
    private static final int field_44768 = 96;
    private static final int field_44769 = 128;
    private final GlyphContainer<UnicodeTextureGlyph> glyphs;

    UnihexFont(GlyphContainer<UnicodeTextureGlyph> glyphs) {
        this.glyphs = glyphs;
    }

    public @Nullable Glyph getGlyph(int codePoint) {
        return (Glyph)this.glyphs.get(codePoint);
    }

    public IntSet getProvidedGlyphs() {
        return this.glyphs.getProvidedGlyphs();
    }

    @VisibleForTesting
    static void addRowPixels(IntBuffer pixelsOut, int row, int left, int right) {
        int i = 32 - left - 1;
        int j = 32 - right - 1;
        for (int k = i; k >= j; --k) {
            if (k >= 32 || k < 0) {
                pixelsOut.put(0);
                continue;
            }
            boolean bl = (row >> k & 1) != 0;
            pixelsOut.put(bl ? -1 : 0);
        }
    }

    static void addGlyphPixels(IntBuffer pixelsOut, BitmapGlyph glyph, int left, int right) {
        for (int i = 0; i < 16; ++i) {
            int j = glyph.getPixels(i);
            UnihexFont.addRowPixels((IntBuffer)pixelsOut, (int)j, (int)left, (int)right);
        }
    }

    @VisibleForTesting
    static void readLines(InputStream stream, BitmapGlyphConsumer callback) throws IOException {
        int i = 0;
        ByteArrayList byteList = new ByteArrayList(128);
        while (true) {
            int l;
            boolean bl = UnihexFont.readUntilDelimiter((InputStream)stream, (ByteList)byteList, (int)58);
            int j = byteList.size();
            if (j == 0 && !bl) break;
            if (!bl || j != 4 && j != 5 && j != 6) {
                throw new IllegalArgumentException("Invalid entry at line " + i + ": expected 4, 5 or 6 hex digits followed by a colon");
            }
            int k = 0;
            for (l = 0; l < j; ++l) {
                k = k << 4 | UnihexFont.getHexDigitValue((int)i, (byte)byteList.getByte(l));
            }
            byteList.clear();
            UnihexFont.readUntilDelimiter((InputStream)stream, (ByteList)byteList, (int)10);
            l = byteList.size();
            BitmapGlyph bitmapGlyph = switch (l) {
                case 32 -> FontImage8x16.read((int)i, (ByteList)byteList);
                case 64 -> FontImage16x16.read((int)i, (ByteList)byteList);
                case 96 -> FontImage32x16.read24x16((int)i, (ByteList)byteList);
                case 128 -> FontImage32x16.read32x16((int)i, (ByteList)byteList);
                default -> throw new IllegalArgumentException("Invalid entry at line " + i + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
            };
            callback.accept(k, bitmapGlyph);
            ++i;
            byteList.clear();
        }
    }

    static int getHexDigitValue(int lineNum, ByteList bytes, int index) {
        return UnihexFont.getHexDigitValue((int)lineNum, (byte)bytes.getByte(index));
    }

    private static int getHexDigitValue(int lineNum, byte digit) {
        return switch (digit) {
            case 48 -> 0;
            case 49 -> 1;
            case 50 -> 2;
            case 51 -> 3;
            case 52 -> 4;
            case 53 -> 5;
            case 54 -> 6;
            case 55 -> 7;
            case 56 -> 8;
            case 57 -> 9;
            case 65 -> 10;
            case 66 -> 11;
            case 67 -> 12;
            case 68 -> 13;
            case 69 -> 14;
            case 70 -> 15;
            default -> throw new IllegalArgumentException("Invalid entry at line " + lineNum + ": expected hex digit, got " + (char)digit);
        };
    }

    private static boolean readUntilDelimiter(InputStream stream, ByteList data, int delimiter) throws IOException {
        int i;
        while ((i = stream.read()) != -1) {
            if (i == delimiter) {
                return true;
            }
            data.add((byte)i);
        }
        return false;
    }
}

