/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyphImpl
 *  net.minecraft.client.font.BuiltinEmptyGlyph
 *  net.minecraft.client.font.BuiltinEmptyGlyph$ColorSupplier
 *  net.minecraft.client.font.GlyphBaker
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.client.font.UploadableGlyph
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImage$Format
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyphImpl;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.GlyphBaker;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class BuiltinEmptyGlyph
extends Enum<BuiltinEmptyGlyph>
implements GlyphMetrics {
    public static final /* enum */ BuiltinEmptyGlyph WHITE = new BuiltinEmptyGlyph("WHITE", 0, () -> BuiltinEmptyGlyph.createRectImage((int)5, (int)8, (x, y) -> -1));
    public static final /* enum */ BuiltinEmptyGlyph MISSING = new BuiltinEmptyGlyph("MISSING", 1, () -> {
        int i = 5;
        int j = 8;
        return BuiltinEmptyGlyph.createRectImage((int)5, (int)8, (x, y) -> {
            boolean bl = x == 0 || x + 1 == 5 || y == 0 || y + 1 == 8;
            return bl ? -1 : 0;
        });
    });
    final NativeImage image;
    private static final /* synthetic */ BuiltinEmptyGlyph[] field_37901;

    public static BuiltinEmptyGlyph[] values() {
        return (BuiltinEmptyGlyph[])field_37901.clone();
    }

    public static BuiltinEmptyGlyph valueOf(String string) {
        return Enum.valueOf(BuiltinEmptyGlyph.class, string);
    }

    private static NativeImage createRectImage(int width, int height, ColorSupplier colorSupplier) {
        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                nativeImage.setColorArgb(j, i, colorSupplier.getColor(j, i));
            }
        }
        nativeImage.untrack();
        return nativeImage;
    }

    private BuiltinEmptyGlyph(Supplier<NativeImage> imageSupplier) {
        this.image = imageSupplier.get();
    }

    public float getAdvance() {
        return this.image.getWidth() + 1;
    }

    public @Nullable BakedGlyphImpl bake(GlyphBaker glyphBaker) {
        return glyphBaker.bake((GlyphMetrics)this, (UploadableGlyph)new /* Unavailable Anonymous Inner Class!! */);
    }

    private static /* synthetic */ BuiltinEmptyGlyph[] method_41838() {
        return new BuiltinEmptyGlyph[]{WHITE, MISSING};
    }

    static {
        field_37901 = BuiltinEmptyGlyph.method_41838();
    }
}

