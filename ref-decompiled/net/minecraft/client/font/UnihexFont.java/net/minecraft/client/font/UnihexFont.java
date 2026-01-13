/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  it.unimi.dsi.fastutil.bytes.ByteList
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

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

    @Override
    public @Nullable Glyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    @Override
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
            UnihexFont.addRowPixels(pixelsOut, j, left, right);
        }
    }

    @VisibleForTesting
    static void readLines(InputStream stream, BitmapGlyphConsumer callback) throws IOException {
        int i = 0;
        ByteArrayList byteList = new ByteArrayList(128);
        while (true) {
            int l;
            boolean bl = UnihexFont.readUntilDelimiter(stream, (ByteList)byteList, 58);
            int j = byteList.size();
            if (j == 0 && !bl) break;
            if (!bl || j != 4 && j != 5 && j != 6) {
                throw new IllegalArgumentException("Invalid entry at line " + i + ": expected 4, 5 or 6 hex digits followed by a colon");
            }
            int k = 0;
            for (l = 0; l < j; ++l) {
                k = k << 4 | UnihexFont.getHexDigitValue(i, byteList.getByte(l));
            }
            byteList.clear();
            UnihexFont.readUntilDelimiter(stream, (ByteList)byteList, 10);
            l = byteList.size();
            BitmapGlyph bitmapGlyph = switch (l) {
                case 32 -> FontImage8x16.read(i, (ByteList)byteList);
                case 64 -> FontImage16x16.read(i, (ByteList)byteList);
                case 96 -> FontImage32x16.read24x16(i, (ByteList)byteList);
                case 128 -> FontImage32x16.read32x16(i, (ByteList)byteList);
                default -> throw new IllegalArgumentException("Invalid entry at line " + i + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
            };
            callback.accept(k, bitmapGlyph);
            ++i;
            byteList.clear();
        }
    }

    static int getHexDigitValue(int lineNum, ByteList bytes, int index) {
        return UnihexFont.getHexDigitValue(lineNum, bytes.getByte(index));
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

    @Environment(value=EnvType.CLIENT)
    public static interface BitmapGlyph {
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
            return Dimensions.pack(k, l);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record FontImage8x16(byte[] contents) implements BitmapGlyph
    {
        @Override
        public int getPixels(int y) {
            return this.contents[y] << 24;
        }

        static BitmapGlyph read(int lineNum, ByteList data) {
            byte[] bs = new byte[16];
            int i = 0;
            for (int j = 0; j < 16; ++j) {
                byte b;
                int k = UnihexFont.getHexDigitValue(lineNum, data, i++);
                int l = UnihexFont.getHexDigitValue(lineNum, data, i++);
                bs[j] = b = (byte)(k << 4 | l);
            }
            return new FontImage8x16(bs);
        }

        @Override
        public int bitWidth() {
            return 8;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record FontImage16x16(short[] contents) implements BitmapGlyph
    {
        @Override
        public int getPixels(int y) {
            return this.contents[y] << 16;
        }

        static BitmapGlyph read(int lineNum, ByteList data) {
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
            return new FontImage16x16(ss);
        }

        @Override
        public int bitWidth() {
            return 16;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record FontImage32x16(int[] contents, int bitWidth) implements BitmapGlyph
    {
        private static final int field_44775 = 24;

        @Override
        public int getPixels(int y) {
            return this.contents[y];
        }

        static BitmapGlyph read24x16(int lineNum, ByteList data) {
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
            return new FontImage32x16(is, 24);
        }

        public static BitmapGlyph read32x16(int lineNum, ByteList data) {
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
            return new FontImage32x16(is, 32);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface BitmapGlyphConsumer {
        public void accept(int var1, BitmapGlyph var2);
    }

    @Environment(value=EnvType.CLIENT)
    static final class UnicodeTextureGlyph
    extends Record
    implements Glyph {
        final BitmapGlyph contents;
        final int left;
        final int right;

        UnicodeTextureGlyph(BitmapGlyph contents, int left, int right) {
            this.contents = contents;
            this.left = left;
            this.right = right;
        }

        public int width() {
            return this.right - this.left + 1;
        }

        @Override
        public GlyphMetrics getMetrics() {
            return new GlyphMetrics(){

                @Override
                public float getAdvance() {
                    return this.width() / 2 + 1;
                }

                @Override
                public float getShadowOffset() {
                    return 0.5f;
                }

                @Override
                public float getBoldOffset() {
                    return 0.5f;
                }
            };
        }

        @Override
        public BakedGlyph bake(Glyph.AbstractGlyphBaker baker) {
            return baker.bake(this.getMetrics(), new UploadableGlyph(){

                @Override
                public float getOversample() {
                    return 2.0f;
                }

                @Override
                public int getWidth() {
                    return this.width();
                }

                @Override
                public int getHeight() {
                    return 16;
                }

                @Override
                public void upload(int x, int y, GpuTexture texture) {
                    IntBuffer intBuffer = MemoryUtil.memAllocInt((int)(this.width() * 16));
                    UnihexFont.addGlyphPixels(intBuffer, contents, left, right);
                    intBuffer.rewind();
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, MemoryUtil.memByteBuffer((IntBuffer)intBuffer), NativeImage.Format.RGBA, 0, 0, x, y, this.width(), 16);
                    MemoryUtil.memFree((Buffer)intBuffer);
                }

                @Override
                public boolean hasColor() {
                    return true;
                }
            });
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{UnicodeTextureGlyph.class, "contents;left;right", "contents", "left", "right"}, this, object);
        }

        public BitmapGlyph contents() {
            return this.contents;
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        public static final MapCodec<Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("hex_file").forGetter(loader -> loader.sizes), (App)DimensionOverride.CODEC.listOf().optionalFieldOf("size_overrides", List.of()).forGetter(loader -> loader.overrides)).apply((Applicative)instance, Loader::new));
        private final Identifier sizes;
        private final List<DimensionOverride> overrides;

        private Loader(Identifier sizes, List<DimensionOverride> overrides) {
            this.sizes = sizes;
            this.overrides = overrides;
        }

        @Override
        public FontType getType() {
            return FontType.UNIHEX;
        }

        @Override
        public Either<FontLoader.Loadable, FontLoader.Reference> build() {
            return Either.left(this::load);
        }

        private Font load(ResourceManager resourceManager) throws IOException {
            try (InputStream inputStream = resourceManager.open(this.sizes);){
                UnihexFont unihexFont = this.loadHexFile(inputStream);
                return unihexFont;
            }
        }

        private UnihexFont loadHexFile(InputStream stream) throws IOException {
            GlyphContainer<BitmapGlyph> glyphContainer = new GlyphContainer<BitmapGlyph>(BitmapGlyph[]::new, rows -> new BitmapGlyph[rows][]);
            BitmapGlyphConsumer bitmapGlyphConsumer = glyphContainer::put;
            try (ZipInputStream zipInputStream = new ZipInputStream(stream);){
                ZipEntry zipEntry;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String string = zipEntry.getName();
                    if (!string.endsWith(".hex")) continue;
                    LOGGER.info("Found {}, loading", (Object)string);
                    UnihexFont.readLines(new FixedBufferInputStream(zipInputStream), bitmapGlyphConsumer);
                }
                GlyphContainer<UnicodeTextureGlyph> glyphContainer2 = new GlyphContainer<UnicodeTextureGlyph>(UnicodeTextureGlyph[]::new, i -> new UnicodeTextureGlyph[i][]);
                for (DimensionOverride dimensionOverride : this.overrides) {
                    int i2 = dimensionOverride.from;
                    int j = dimensionOverride.to;
                    Dimensions dimensions = dimensionOverride.dimensions;
                    for (int k = i2; k <= j; ++k) {
                        BitmapGlyph bitmapGlyph = (BitmapGlyph)glyphContainer.remove(k);
                        if (bitmapGlyph == null) continue;
                        glyphContainer2.put(k, new UnicodeTextureGlyph(bitmapGlyph, dimensions.left, dimensions.right));
                    }
                }
                glyphContainer.forEachGlyph((codePoint, glyph) -> {
                    int i = glyph.getPackedDimensions();
                    int j = Dimensions.getLeft(i);
                    int k = Dimensions.getRight(i);
                    glyphContainer2.put(codePoint, new UnicodeTextureGlyph((BitmapGlyph)glyph, j, k));
                });
                UnihexFont unihexFont = new UnihexFont(glyphContainer2);
                return unihexFont;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Dimensions
    extends Record {
        final int left;
        final int right;
        public static final MapCodec<Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.fieldOf("left").forGetter(Dimensions::left), (App)Codec.INT.fieldOf("right").forGetter(Dimensions::right)).apply((Applicative)instance, Dimensions::new));
        public static final Codec<Dimensions> CODEC = MAP_CODEC.codec();

        public Dimensions(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public int packedValue() {
            return Dimensions.pack(this.left, this.right);
        }

        public static int pack(int left, int right) {
            return (left & 0xFF) << 8 | right & 0xFF;
        }

        public static int getLeft(int packed) {
            return (byte)(packed >> 8);
        }

        public static int getRight(int packed) {
            return (byte)packed;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Dimensions.class, "left;right", "left", "right"}, this, object);
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class DimensionOverride
    extends Record {
        final int from;
        final int to;
        final Dimensions dimensions;
        private static final Codec<DimensionOverride> NON_VALIDATED_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.CODEPOINT.fieldOf("from").forGetter(DimensionOverride::from), (App)Codecs.CODEPOINT.fieldOf("to").forGetter(DimensionOverride::to), (App)Dimensions.MAP_CODEC.forGetter(DimensionOverride::dimensions)).apply((Applicative)instance, DimensionOverride::new));
        public static final Codec<DimensionOverride> CODEC = NON_VALIDATED_CODEC.validate(override -> {
            if (override.from >= override.to) {
                return DataResult.error(() -> "Invalid range: [" + dimensionOverride.from + ";" + dimensionOverride.to + "]");
            }
            return DataResult.success((Object)override);
        });

        private DimensionOverride(int from, int to, Dimensions dimensions) {
            this.from = from;
            this.to = to;
            this.dimensions = dimensions;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DimensionOverride.class, "from;to;dimensions", "from", "to", "dimensions"}, this, object);
        }

        public int from() {
            return this.from;
        }

        public int to() {
            return this.to;
        }

        public Dimensions dimensions() {
            return this.dimensions;
        }
    }
}
