package net.minecraft.client.font;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

@Environment(EnvType.CLIENT)
public record TrueTypeFontLoader(Identifier location, float size, float oversample, Shift shift, String skip) implements FontLoader {
   private static final Codec SKIP_CODEC;
   public static final MapCodec CODEC;

   public TrueTypeFontLoader(Identifier identifier, float f, float g, Shift shift, String string) {
      this.location = identifier;
      this.size = f;
      this.oversample = g;
      this.shift = shift;
      this.skip = string;
   }

   public FontType getType() {
      return FontType.TTF;
   }

   public Either build() {
      return Either.left(this::load);
   }

   private Font load(ResourceManager resourceManager) throws IOException {
      FT_Face fT_Face = null;
      ByteBuffer byteBuffer = null;

      try {
         InputStream inputStream = resourceManager.open(this.location.withPrefixedPath("font/"));

         TrueTypeFont var19;
         try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.flip();
            synchronized(FreeTypeUtil.LOCK) {
               MemoryStack memoryStack = MemoryStack.stackPush();

               try {
                  PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                  FreeTypeUtil.checkFatalError(FreeType.FT_New_Memory_Face(FreeTypeUtil.initialize(), byteBuffer, 0L, pointerBuffer), "Initializing font face");
                  fT_Face = FT_Face.create(pointerBuffer.get());
               } catch (Throwable var14) {
                  if (memoryStack != null) {
                     try {
                        memoryStack.close();
                     } catch (Throwable var12) {
                        var14.addSuppressed(var12);
                     }
                  }

                  throw var14;
               }

               if (memoryStack != null) {
                  memoryStack.close();
               }

               String string = FreeType.FT_Get_Font_Format(fT_Face);
               if (!"TrueType".equals(string)) {
                  throw new IOException("Font is not in TTF format, was " + string);
               }

               FreeTypeUtil.checkFatalError(FreeType.FT_Select_Charmap(fT_Face, FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
               var19 = new TrueTypeFont(byteBuffer, fT_Face, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
            }
         } catch (Throwable var16) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }
            }

            throw var16;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var19;
      } catch (Exception var17) {
         synchronized(FreeTypeUtil.LOCK) {
            if (fT_Face != null) {
               FreeType.FT_Done_Face(fT_Face);
            }
         }

         MemoryUtil.memFree(byteBuffer);
         throw var17;
      }
   }

   public Identifier location() {
      return this.location;
   }

   public float size() {
      return this.size;
   }

   public float oversample() {
      return this.oversample;
   }

   public Shift shift() {
      return this.shift;
   }

   public String skip() {
      return this.skip;
   }

   static {
      SKIP_CODEC = Codec.withAlternative(Codec.STRING, Codec.STRING.listOf(), (chars) -> {
         return String.join("", chars);
      });
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("file").forGetter(TrueTypeFontLoader::location), Codec.FLOAT.optionalFieldOf("size", 11.0F).forGetter(TrueTypeFontLoader::size), Codec.FLOAT.optionalFieldOf("oversample", 1.0F).forGetter(TrueTypeFontLoader::oversample), TrueTypeFontLoader.Shift.CODEC.optionalFieldOf("shift", TrueTypeFontLoader.Shift.NONE).forGetter(TrueTypeFontLoader::shift), SKIP_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeFontLoader::skip)).apply(instance, TrueTypeFontLoader::new);
      });
   }

   @Environment(EnvType.CLIENT)
   public static record Shift(float x, float y) {
      final float x;
      final float y;
      public static final Shift NONE = new Shift(0.0F, 0.0F);
      public static final Codec CODEC = Codec.floatRange(-512.0F, 512.0F).listOf().comapFlatMap((floatList) -> {
         return Util.decodeFixedLengthList(floatList, 2).map((floatListx) -> {
            return new Shift((Float)floatListx.get(0), (Float)floatListx.get(1));
         });
      }, (shift) -> {
         return List.of(shift.x, shift.y);
      });

      public Shift(float f, float g) {
         this.x = f;
         this.y = g;
      }

      public float x() {
         return this.x;
      }

      public float y() {
         return this.y;
      }
   }
}
