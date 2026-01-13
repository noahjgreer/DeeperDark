/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.EmptyGlyph
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.FreeTypeUtil
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.GlyphContainer
 *  net.minecraft.client.font.TrueTypeFont
 *  net.minecraft.client.font.TrueTypeFont$LazyGlyph
 *  net.minecraft.client.font.TrueTypeFont$TtfGlyph
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Bitmap
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FT_GlyphSlot
 *  org.lwjgl.util.freetype.FT_Vector
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.TrueTypeFont;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    private @Nullable ByteBuffer buffer;
    private @Nullable FT_Face face;
    final float oversample;
    private final GlyphContainer<LazyGlyph> container = new GlyphContainer(LazyGlyph[]::new, i -> new LazyGlyph[i][]);

    public TrueTypeFont(ByteBuffer buffer, FT_Face face, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
        this.buffer = buffer;
        this.face = face;
        this.oversample = oversample;
        IntArraySet intSet = new IntArraySet();
        excludedCharacters.codePoints().forEach(arg_0 -> ((IntSet)intSet).add(arg_0));
        int i2 = Math.round(size * oversample);
        FreeType.FT_Set_Pixel_Sizes((FT_Face)face, (int)i2, (int)i2);
        float f = shiftX * oversample;
        float g = -shiftY * oversample;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int k;
            FT_Vector fT_Vector = FreeTypeUtil.set((FT_Vector)FT_Vector.malloc((MemoryStack)memoryStack), (float)f, (float)g);
            FreeType.FT_Set_Transform((FT_Face)face, null, (FT_Vector)fT_Vector);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            int j = (int)FreeType.FT_Get_First_Char((FT_Face)face, (IntBuffer)intBuffer);
            while ((k = intBuffer.get(0)) != 0) {
                if (!intSet.contains(j)) {
                    this.container.put(j, (Object)new LazyGlyph(k));
                }
                j = (int)FreeType.FT_Get_Next_Char((FT_Face)face, (long)j, (IntBuffer)intBuffer);
            }
        }
    }

    public @Nullable Glyph getGlyph(int codePoint) {
        LazyGlyph lazyGlyph = (LazyGlyph)this.container.get(codePoint);
        return lazyGlyph != null ? this.getOrLoadGlyph(codePoint, lazyGlyph) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Glyph getOrLoadGlyph(int codePoint, LazyGlyph glyph) {
        Glyph glyph2 = glyph.glyph;
        if (glyph2 == null) {
            FT_Face fT_Face;
            FT_Face fT_Face2 = fT_Face = this.getInfo();
            synchronized (fT_Face2) {
                glyph2 = glyph.glyph;
                if (glyph2 == null) {
                    glyph.glyph = glyph2 = this.loadGlyph(codePoint, fT_Face, glyph.index);
                }
            }
        }
        return glyph2;
    }

    private Glyph loadGlyph(int codePoint, FT_Face face, int index) {
        FT_GlyphSlot fT_GlyphSlot;
        int i = FreeType.FT_Load_Glyph((FT_Face)face, (int)index, (int)0x400008);
        if (i != 0) {
            FreeTypeUtil.checkFatalError((int)i, (String)String.format(Locale.ROOT, "Loading glyph U+%06X", codePoint));
        }
        if ((fT_GlyphSlot = face.glyph()) == null) {
            throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", codePoint));
        }
        float f = FreeTypeUtil.getX((FT_Vector)fT_GlyphSlot.advance());
        FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
        int j = fT_GlyphSlot.bitmap_left();
        int k = fT_GlyphSlot.bitmap_top();
        int l = fT_Bitmap.width();
        int m = fT_Bitmap.rows();
        if (l <= 0 || m <= 0) {
            return new EmptyGlyph(f / this.oversample);
        }
        return new TtfGlyph(this, (float)j, (float)k, l, m, f, index);
    }

    FT_Face getInfo() {
        if (this.buffer == null || this.face == null) {
            throw new IllegalStateException("Provider already closed");
        }
        return this.face;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        if (this.face != null) {
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                FreeTypeUtil.checkError((int)FreeType.FT_Done_Face((FT_Face)this.face), (String)"Deleting face");
            }
            this.face = null;
        }
        MemoryUtil.memFree((Buffer)this.buffer);
        this.buffer = null;
    }

    public IntSet getProvidedGlyphs() {
        return this.container.getProvidedGlyphs();
    }
}

