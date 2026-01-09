package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BitmapFont implements Font {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final GlyphContainer glyphs;

   BitmapFont(NativeImage image, GlyphContainer glyphs) {
      this.image = image;
      this.glyphs = glyphs;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public Glyph getGlyph(int codePoint) {
      return (Glyph)this.glyphs.get(codePoint);
   }

   public IntSet getProvidedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.getProvidedGlyphs());
   }

   @Environment(EnvType.CLIENT)
   private static record BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) implements Glyph {
      final float scaleFactor;
      final NativeImage image;
      final int x;
      final int y;
      final int width;
      final int height;
      final int ascent;

      BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
         this.scaleFactor = scaleFactor;
         this.image = image;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.advance = advance;
         this.ascent = ascent;
      }

      public float getAdvance() {
         return (float)this.advance;
      }

      public BakedGlyph bake(Function function) {
         return (BakedGlyph)function.apply(new RenderableGlyph() {
            public float getOversample() {
               return 1.0F / BitmapFontGlyph.this.scaleFactor;
            }

            public int getWidth() {
               return BitmapFontGlyph.this.width;
            }

            public int getHeight() {
               return BitmapFontGlyph.this.height;
            }

            public float getAscent() {
               return (float)BitmapFontGlyph.this.ascent;
            }

            public void upload(int x, int y, GpuTexture texture) {
               RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, BitmapFontGlyph.this.image, 0, 0, x, y, BitmapFontGlyph.this.width, BitmapFontGlyph.this.height, BitmapFontGlyph.this.x, BitmapFontGlyph.this.y);
            }

            public boolean hasColor() {
               return BitmapFontGlyph.this.image.getFormat().getChannelCount() > 1;
            }
         });
      }

      public float scaleFactor() {
         return this.scaleFactor;
      }

      public NativeImage image() {
         return this.image;
      }

      public int x() {
         return this.x;
      }

      public int y() {
         return this.y;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public int advance() {
         return this.advance;
      }

      public int ascent() {
         return this.ascent;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Loader(Identifier file, int height, int ascent, int[][] codepointGrid) implements FontLoader {
      private static final Codec CODE_POINT_GRID_CODEC;
      public static final MapCodec CODEC;

      public Loader(Identifier id, int height, int ascent, int[][] is) {
         this.file = id;
         this.height = height;
         this.ascent = ascent;
         this.codepointGrid = is;
      }

      private static DataResult validateCodePointGrid(int[][] codePointGrid) {
         int i = codePointGrid.length;
         if (i == 0) {
            return DataResult.error(() -> {
               return "Expected to find data in codepoint grid";
            });
         } else {
            int[] is = codePointGrid[0];
            int j = is.length;
            if (j == 0) {
               return DataResult.error(() -> {
                  return "Expected to find data in codepoint grid";
               });
            } else {
               for(int k = 1; k < i; ++k) {
                  int[] js = codePointGrid[k];
                  if (js.length != j) {
                     return DataResult.error(() -> {
                        return "Lines in codepoint grid have to be the same length (found: " + js.length + " codepoints, expected: " + j + "), pad with \\u0000";
                     });
                  }
               }

               return DataResult.success(codePointGrid);
            }
         }
      }

      private static DataResult validate(Loader fontLoader) {
         return fontLoader.ascent > fontLoader.height ? DataResult.error(() -> {
            return "Ascent " + fontLoader.ascent + " higher than height " + fontLoader.height;
         }) : DataResult.success(fontLoader);
      }

      public FontType getType() {
         return FontType.BITMAP;
      }

      public Either build() {
         return Either.left(this::load);
      }

      private Font load(ResourceManager resourceManager) throws IOException {
         Identifier identifier = this.file.withPrefixedPath("textures/");
         InputStream inputStream = resourceManager.open(identifier);

         BitmapFont var22;
         try {
            NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
            int i = nativeImage.getWidth();
            int j = nativeImage.getHeight();
            int k = i / this.codepointGrid[0].length;
            int l = j / this.codepointGrid.length;
            float f = (float)this.height / (float)l;
            GlyphContainer glyphContainer = new GlyphContainer((ix) -> {
               return new BitmapFontGlyph[ix];
            }, (ix) -> {
               return new BitmapFontGlyph[ix][];
            });
            int m = 0;

            while(true) {
               if (m >= this.codepointGrid.length) {
                  var22 = new BitmapFont(nativeImage, glyphContainer);
                  break;
               }

               int n = 0;
               int[] var13 = this.codepointGrid[m];
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  int o = var13[var15];
                  int p = n++;
                  if (o != 0) {
                     int q = this.findCharacterStartX(nativeImage, k, l, p, m);
                     BitmapFontGlyph bitmapFontGlyph = (BitmapFontGlyph)glyphContainer.put(o, new BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)q * f)) + 1, this.ascent));
                     if (bitmapFontGlyph != null) {
                        BitmapFont.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(o), identifier);
                     }
                  }
               }

               ++m;
            }
         } catch (Throwable var21) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var20) {
                  var21.addSuppressed(var20);
               }
            }

            throw var21;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var22;
      }

      private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
         int i;
         for(i = characterWidth - 1; i >= 0; --i) {
            int j = charPosX * characterWidth + i;

            for(int k = 0; k < characterHeight; ++k) {
               int l = charPosY * characterHeight + k;
               if (image.getOpacity(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }

      public Identifier file() {
         return this.file;
      }

      public int height() {
         return this.height;
      }

      public int ascent() {
         return this.ascent;
      }

      public int[][] codepointGrid() {
         return this.codepointGrid;
      }

      static {
         CODE_POINT_GRID_CODEC = Codec.STRING.listOf().xmap((strings) -> {
            int i = strings.size();
            int[][] is = new int[i][];

            for(int j = 0; j < i; ++j) {
               is[j] = ((String)strings.get(j)).codePoints().toArray();
            }

            return is;
         }, (codePointGrid) -> {
            List list = new ArrayList(codePointGrid.length);
            int[][] var2 = codePointGrid;
            int var3 = codePointGrid.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               int[] is = var2[var4];
               list.add(new String(is, 0, is.length));
            }

            return list;
         }).validate(Loader::validateCodePointGrid);
         CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Identifier.CODEC.fieldOf("file").forGetter(Loader::file), Codec.INT.optionalFieldOf("height", 8).forGetter(Loader::height), Codec.INT.fieldOf("ascent").forGetter(Loader::ascent), CODE_POINT_GRID_CODEC.fieldOf("chars").forGetter(Loader::codepointGrid)).apply(instance, Loader::new);
         }).validate(Loader::validate);
      }
   }
}
