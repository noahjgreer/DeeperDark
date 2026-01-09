package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface Font extends AutoCloseable {
   float field_48382 = 7.0F;

   default void close() {
   }

   @Nullable
   default Glyph getGlyph(int codePoint) {
      return null;
   }

   IntSet getProvidedGlyphs();

   @Environment(EnvType.CLIENT)
   public static record FontFilterPair(Font provider, FontFilterType.FilterMap filter) implements AutoCloseable {
      public FontFilterPair(Font font, FontFilterType.FilterMap filterMap) {
         this.provider = font;
         this.filter = filterMap;
      }

      public void close() {
         this.provider.close();
      }

      public Font provider() {
         return this.provider;
      }

      public FontFilterType.FilterMap filter() {
         return this.filter;
      }
   }
}
