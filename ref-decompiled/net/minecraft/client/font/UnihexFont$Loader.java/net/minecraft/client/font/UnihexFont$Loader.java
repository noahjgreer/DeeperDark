/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.UnihexFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static class UnihexFont.Loader
implements FontLoader {
    public static final MapCodec<UnihexFont.Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("hex_file").forGetter(loader -> loader.sizes), (App)UnihexFont.DimensionOverride.CODEC.listOf().optionalFieldOf("size_overrides", List.of()).forGetter(loader -> loader.overrides)).apply((Applicative)instance, UnihexFont.Loader::new));
    private final Identifier sizes;
    private final List<UnihexFont.DimensionOverride> overrides;

    private UnihexFont.Loader(Identifier sizes, List<UnihexFont.DimensionOverride> overrides) {
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
        GlyphContainer<UnihexFont.BitmapGlyph> glyphContainer = new GlyphContainer<UnihexFont.BitmapGlyph>(UnihexFont.BitmapGlyph[]::new, rows -> new UnihexFont.BitmapGlyph[rows][]);
        UnihexFont.BitmapGlyphConsumer bitmapGlyphConsumer = glyphContainer::put;
        try (ZipInputStream zipInputStream = new ZipInputStream(stream);){
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String string = zipEntry.getName();
                if (!string.endsWith(".hex")) continue;
                LOGGER.info("Found {}, loading", (Object)string);
                UnihexFont.readLines(new FixedBufferInputStream(zipInputStream), bitmapGlyphConsumer);
            }
            GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphContainer2 = new GlyphContainer<UnihexFont.UnicodeTextureGlyph>(UnihexFont.UnicodeTextureGlyph[]::new, i -> new UnihexFont.UnicodeTextureGlyph[i][]);
            for (UnihexFont.DimensionOverride dimensionOverride : this.overrides) {
                int i2 = dimensionOverride.from;
                int j = dimensionOverride.to;
                UnihexFont.Dimensions dimensions = dimensionOverride.dimensions;
                for (int k = i2; k <= j; ++k) {
                    UnihexFont.BitmapGlyph bitmapGlyph = (UnihexFont.BitmapGlyph)glyphContainer.remove(k);
                    if (bitmapGlyph == null) continue;
                    glyphContainer2.put(k, new UnihexFont.UnicodeTextureGlyph(bitmapGlyph, dimensions.left, dimensions.right));
                }
            }
            glyphContainer.forEachGlyph((codePoint, glyph) -> {
                int i = glyph.getPackedDimensions();
                int j = UnihexFont.Dimensions.getLeft(i);
                int k = UnihexFont.Dimensions.getRight(i);
                glyphContainer2.put(codePoint, new UnihexFont.UnicodeTextureGlyph((UnihexFont.BitmapGlyph)glyph, j, k));
            });
            UnihexFont unihexFont = new UnihexFont(glyphContainer2);
            return unihexFont;
        }
    }
}
