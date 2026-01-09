package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Random RANDOM = Random.create();
   private static final float MAX_ADVANCE = 32.0F;
   private final TextureManager textureManager;
   private final Identifier id;
   private BakedGlyph blankBakedGlyph;
   private BakedGlyph whiteRectangleBakedGlyph;
   private List allFonts = List.of();
   private List availableFonts = List.of();
   private final GlyphContainer bakedGlyphCache = new GlyphContainer((i) -> {
      return new BakedGlyph[i];
   }, (rowCount) -> {
      return new BakedGlyph[rowCount][];
   });
   private final GlyphContainer glyphCache = new GlyphContainer((i) -> {
      return new GlyphPair[i];
   }, (rowCount) -> {
      return new GlyphPair[rowCount][];
   });
   private final Int2ObjectMap charactersByWidth = new Int2ObjectOpenHashMap();
   private final List glyphAtlases = Lists.newArrayList();
   private final IntFunction glyphFinder = this::findGlyph;
   private final IntFunction glyphBaker = this::bake;

   public FontStorage(TextureManager textureManager, Identifier id) {
      this.textureManager = textureManager;
      this.id = id;
   }

   public void setFonts(List allFonts, Set activeFilters) {
      this.allFonts = allFonts;
      this.setActiveFilters(activeFilters);
   }

   public void setActiveFilters(Set activeFilters) {
      this.availableFonts = List.of();
      this.clear();
      this.availableFonts = this.applyFilters(this.allFonts, activeFilters);
   }

   private void clear() {
      this.glyphAtlases.clear();
      this.bakedGlyphCache.clear();
      this.glyphCache.clear();
      this.charactersByWidth.clear();
      this.blankBakedGlyph = BuiltinEmptyGlyph.MISSING.bake(this::bake);
      this.whiteRectangleBakedGlyph = BuiltinEmptyGlyph.WHITE.bake(this::bake);
   }

   private List applyFilters(List allFonts, Set activeFilters) {
      IntSet intSet = new IntOpenHashSet();
      List list = new ArrayList();
      Iterator var5 = allFonts.iterator();

      while(var5.hasNext()) {
         Font.FontFilterPair fontFilterPair = (Font.FontFilterPair)var5.next();
         if (fontFilterPair.filter().isAllowed(activeFilters)) {
            list.add(fontFilterPair.provider());
            intSet.addAll(fontFilterPair.provider().getProvidedGlyphs());
         }
      }

      Set set = Sets.newHashSet();
      intSet.forEach((codePoint) -> {
         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            Font font = (Font)var4.next();
            Glyph glyph = font.getGlyph(codePoint);
            if (glyph != null) {
               set.add(font);
               if (glyph != BuiltinEmptyGlyph.MISSING) {
                  ((IntList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), (i) -> {
                     return new IntArrayList();
                  })).add(codePoint);
               }
               break;
            }
         }

      });
      Stream var10000 = list.stream();
      Objects.requireNonNull(set);
      return var10000.filter(set::contains).toList();
   }

   public void close() {
      this.glyphAtlases.clear();
   }

   private static boolean isAdvanceInvalid(Glyph glyph) {
      float f = glyph.getAdvance(false);
      if (!(f < 0.0F) && !(f > 32.0F)) {
         float g = glyph.getAdvance(true);
         return g < 0.0F || g > 32.0F;
      } else {
         return true;
      }
   }

   private GlyphPair findGlyph(int codePoint) {
      Glyph glyph = null;
      Iterator var3 = this.availableFonts.iterator();

      while(var3.hasNext()) {
         Font font = (Font)var3.next();
         Glyph glyph2 = font.getGlyph(codePoint);
         if (glyph2 != null) {
            if (glyph == null) {
               glyph = glyph2;
            }

            if (!isAdvanceInvalid(glyph2)) {
               return new GlyphPair(glyph, glyph2);
            }
         }
      }

      if (glyph != null) {
         return new GlyphPair(glyph, BuiltinEmptyGlyph.MISSING);
      } else {
         return FontStorage.GlyphPair.MISSING;
      }
   }

   public Glyph getGlyph(int codePoint, boolean validateAdvance) {
      return ((GlyphPair)this.glyphCache.computeIfAbsent(codePoint, this.glyphFinder)).getGlyph(validateAdvance);
   }

   private BakedGlyph bake(int codePoint) {
      Iterator var2 = this.availableFonts.iterator();

      Glyph glyph;
      do {
         if (!var2.hasNext()) {
            LOGGER.warn("Couldn't find glyph for character {} (\\u{})", Character.toString(codePoint), String.format("%04x", codePoint));
            return this.blankBakedGlyph;
         }

         Font font = (Font)var2.next();
         glyph = font.getGlyph(codePoint);
      } while(glyph == null);

      return glyph.bake(this::bake);
   }

   public BakedGlyph getBaked(int codePoint) {
      return (BakedGlyph)this.bakedGlyphCache.computeIfAbsent(codePoint, this.glyphBaker);
   }

   private BakedGlyph bake(RenderableGlyph c) {
      Iterator var2 = this.glyphAtlases.iterator();

      BakedGlyph bakedGlyph;
      do {
         if (!var2.hasNext()) {
            Identifier identifier = this.id.withSuffixedPath("/" + this.glyphAtlases.size());
            boolean bl = c.hasColor();
            TextRenderLayerSet textRenderLayerSet = bl ? TextRenderLayerSet.of(identifier) : TextRenderLayerSet.ofIntensity(identifier);
            Objects.requireNonNull(identifier);
            GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(identifier::toString, textRenderLayerSet, bl);
            this.glyphAtlases.add(glyphAtlasTexture2);
            this.textureManager.registerTexture(identifier, (AbstractTexture)glyphAtlasTexture2);
            BakedGlyph bakedGlyph2 = glyphAtlasTexture2.bake(c);
            return bakedGlyph2 == null ? this.blankBakedGlyph : bakedGlyph2;
         }

         GlyphAtlasTexture glyphAtlasTexture = (GlyphAtlasTexture)var2.next();
         bakedGlyph = glyphAtlasTexture.bake(c);
      } while(bakedGlyph == null);

      return bakedGlyph;
   }

   public BakedGlyph getObfuscatedBakedGlyph(Glyph glyph) {
      IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
      return intList != null && !intList.isEmpty() ? this.getBaked(intList.getInt(RANDOM.nextInt(intList.size()))) : this.blankBakedGlyph;
   }

   public Identifier getId() {
      return this.id;
   }

   public BakedGlyph getRectangleBakedGlyph() {
      return this.whiteRectangleBakedGlyph;
   }

   @Environment(EnvType.CLIENT)
   private static record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
      static final GlyphPair MISSING;

      GlyphPair(Glyph glyph, Glyph glyph2) {
         this.glyph = glyph;
         this.advanceValidatedGlyph = glyph2;
      }

      Glyph getGlyph(boolean validateAdvance) {
         return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
      }

      public Glyph glyph() {
         return this.glyph;
      }

      public Glyph advanceValidatedGlyph() {
         return this.advanceValidatedGlyph;
      }

      static {
         MISSING = new GlyphPair(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);
      }
   }
}
