/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record BitmapFont.Loader(Identifier file, int height, int ascent, int[][] codepointGrid) implements FontLoader
{
    private static final Codec<int[][]> CODE_POINT_GRID_CODEC = Codec.STRING.listOf().xmap(strings -> {
        int i = strings.size();
        int[][] is = new int[i][];
        for (int j = 0; j < i; ++j) {
            is[j] = ((String)strings.get(j)).codePoints().toArray();
        }
        return is;
    }, codePointGrid -> {
        ArrayList<String> list = new ArrayList<String>(((int[][])codePointGrid).length);
        for (int[] is : codePointGrid) {
            list.add(new String(is, 0, is.length));
        }
        return list;
    }).validate(BitmapFont.Loader::validateCodePointGrid);
    public static final MapCodec<BitmapFont.Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("file").forGetter(BitmapFont.Loader::file), (App)Codec.INT.optionalFieldOf("height", (Object)8).forGetter(BitmapFont.Loader::height), (App)Codec.INT.fieldOf("ascent").forGetter(BitmapFont.Loader::ascent), (App)CODE_POINT_GRID_CODEC.fieldOf("chars").forGetter(BitmapFont.Loader::codepointGrid)).apply((Applicative)instance, BitmapFont.Loader::new)).validate(BitmapFont.Loader::validate);

    private static DataResult<int[][]> validateCodePointGrid(int[][] codePointGrid) {
        int i = codePointGrid.length;
        if (i == 0) {
            return DataResult.error(() -> "Expected to find data in codepoint grid");
        }
        int[] is = codePointGrid[0];
        int j = is.length;
        if (j == 0) {
            return DataResult.error(() -> "Expected to find data in codepoint grid");
        }
        for (int k = 1; k < i; ++k) {
            int[] js = codePointGrid[k];
            if (js.length == j) continue;
            return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + js.length + " codepoints, expected: " + j + "), pad with \\u0000");
        }
        return DataResult.success((Object)codePointGrid);
    }

    private static DataResult<BitmapFont.Loader> validate(BitmapFont.Loader fontLoader) {
        if (fontLoader.ascent > fontLoader.height) {
            return DataResult.error(() -> "Ascent " + loader.ascent + " higher than height " + loader.height);
        }
        return DataResult.success((Object)fontLoader);
    }

    @Override
    public FontType getType() {
        return FontType.BITMAP;
    }

    @Override
    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.left(this::load);
    }

    private Font load(ResourceManager resourceManager) throws IOException {
        Identifier identifier = this.file.withPrefixedPath("textures/");
        try (InputStream inputStream = resourceManager.open(identifier);){
            NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
            int i2 = nativeImage.getWidth();
            int j = nativeImage.getHeight();
            int k = i2 / this.codepointGrid[0].length;
            int l = j / this.codepointGrid.length;
            float f = (float)this.height / (float)l;
            GlyphContainer<BitmapFont.BitmapFontGlyph> glyphContainer = new GlyphContainer<BitmapFont.BitmapFontGlyph>(BitmapFont.BitmapFontGlyph[]::new, i -> new BitmapFont.BitmapFontGlyph[i][]);
            for (int m = 0; m < this.codepointGrid.length; ++m) {
                int n = 0;
                for (int o : this.codepointGrid[m]) {
                    int q;
                    BitmapFont.BitmapFontGlyph bitmapFontGlyph;
                    int p = n++;
                    if (o == 0 || (bitmapFontGlyph = glyphContainer.put(o, new BitmapFont.BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(nativeImage, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
                    LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)identifier);
                }
            }
            BitmapFont bitmapFont = new BitmapFont(nativeImage, glyphContainer);
            return bitmapFont;
        }
    }

    private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
        int i;
        for (i = characterWidth - 1; i >= 0; --i) {
            int j = charPosX * characterWidth + i;
            for (int k = 0; k < characterHeight; ++k) {
                int l = charPosY * characterHeight + k;
                if (image.getOpacity(j, l) == 0) continue;
                return i + 1;
            }
        }
        return i + 1;
    }
}
