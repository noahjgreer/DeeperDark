package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SpaceFont implements Font {
   private final Int2ObjectMap codePointsToGlyphs;

   public SpaceFont(Map codePointsToAdvances) {
      this.codePointsToGlyphs = new Int2ObjectOpenHashMap(codePointsToAdvances.size());
      codePointsToAdvances.forEach((codePoint, glyph) -> {
         this.codePointsToGlyphs.put(codePoint, () -> {
            return glyph;
         });
      });
   }

   @Nullable
   public Glyph getGlyph(int codePoint) {
      return (Glyph)this.codePointsToGlyphs.get(codePoint);
   }

   public IntSet getProvidedGlyphs() {
      return IntSets.unmodifiable(this.codePointsToGlyphs.keySet());
   }

   @Environment(EnvType.CLIENT)
   public static record Loader(Map advances) implements FontLoader {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.unboundedMap(Codecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(Loader::advances)).apply(instance, Loader::new);
      });

      public Loader(Map map) {
         this.advances = map;
      }

      public FontType getType() {
         return FontType.SPACE;
      }

      public Either build() {
         FontLoader.Loadable loadable = (resourceManager) -> {
            return new SpaceFont(this.advances);
         };
         return Either.left(loadable);
      }

      public Map advances() {
         return this.advances;
      }
   }
}
