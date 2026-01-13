/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.BuiltinEmptyGlyph;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphBaker;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.UploadableGlyph;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final float MAX_ADVANCE = 32.0f;
    private static final BakedGlyph MISSING_GLYPH = new BakedGlyph(){

        @Override
        public GlyphMetrics getMetrics() {
            return BuiltinEmptyGlyph.MISSING;
        }

        @Override
        public  @Nullable TextDrawable.DrawnGlyphRect create(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
            return null;
        }
    };
    final GlyphBaker glyphBaker;
    final Glyph.AbstractGlyphBaker abstractBaker = new Glyph.AbstractGlyphBaker(){

        @Override
        public BakedGlyph bake(GlyphMetrics metrics, UploadableGlyph renderable) {
            return Objects.requireNonNullElse(FontStorage.this.glyphBaker.bake(metrics, renderable), FontStorage.this.blankBakedGlyph);
        }

        @Override
        public BakedGlyph getBlankGlyph() {
            return FontStorage.this.blankBakedGlyph;
        }
    };
    private List<Font.FontFilterPair> allFonts = List.of();
    private List<Font> availableFonts = List.of();
    private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap();
    private final GlyphContainer<GlyphPair> bakedGlyphCache = new GlyphContainer(GlyphPair[]::new, rowCount -> new GlyphPair[rowCount][]);
    private final IntFunction<GlyphPair> findGlyph = this::findGlyph;
    BakedGlyph blankBakedGlyph = MISSING_GLYPH;
    private final Supplier<BakedGlyph> blankGlyphSupplier = () -> this.blankBakedGlyph;
    private final GlyphPair blankBakedGlyphPair = new GlyphPair(this.blankGlyphSupplier, this.blankGlyphSupplier);
    private @Nullable EffectGlyph whiteRectangleBakedGlyph;
    private final GlyphProvider anyGlyphs = new Glyphs(false);
    private final GlyphProvider advanceValidatingGlyphs = new Glyphs(true);

    public FontStorage(GlyphBaker baker) {
        this.glyphBaker = baker;
    }

    public void setFonts(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        this.allFonts = allFonts;
        this.setActiveFilters(activeFilters);
    }

    public void setActiveFilters(Set<FontFilterType> activeFilters) {
        this.availableFonts = List.of();
        this.clear();
        this.availableFonts = this.applyFilters(this.allFonts, activeFilters);
    }

    private void clear() {
        this.glyphBaker.clear();
        this.bakedGlyphCache.clear();
        this.charactersByWidth.clear();
        this.blankBakedGlyph = Objects.requireNonNull(BuiltinEmptyGlyph.MISSING.bake(this.glyphBaker));
        this.whiteRectangleBakedGlyph = BuiltinEmptyGlyph.WHITE.bake(this.glyphBaker);
    }

    private List<Font> applyFilters(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
        IntOpenHashSet intSet = new IntOpenHashSet();
        ArrayList<Font> list = new ArrayList<Font>();
        for (Font.FontFilterPair fontFilterPair : allFonts) {
            if (!fontFilterPair.filter().isAllowed(activeFilters)) continue;
            list.add(fontFilterPair.provider());
            intSet.addAll((IntCollection)fontFilterPair.provider().getProvidedGlyphs());
        }
        HashSet set = Sets.newHashSet();
        intSet.forEach(codePoint -> {
            for (Font font : list) {
                Glyph glyph = font.getGlyph(codePoint);
                if (glyph == null) continue;
                set.add(font);
                if (glyph.getMetrics() == BuiltinEmptyGlyph.MISSING) break;
                ((IntList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getMetrics().getAdvance(false)), i -> new IntArrayList())).add(codePoint);
                break;
            }
        });
        return list.stream().filter(set::contains).toList();
    }

    @Override
    public void close() {
        this.glyphBaker.close();
    }

    private static boolean isAdvanceInvalid(GlyphMetrics glyph) {
        float f = glyph.getAdvance(false);
        if (f < 0.0f || f > 32.0f) {
            return true;
        }
        float g = glyph.getAdvance(true);
        return g < 0.0f || g > 32.0f;
    }

    private GlyphPair findGlyph(int codePoint) {
        LazyBakedGlyph lazyBakedGlyph = null;
        for (Font font : this.availableFonts) {
            Glyph glyph = font.getGlyph(codePoint);
            if (glyph == null) continue;
            if (lazyBakedGlyph == null) {
                lazyBakedGlyph = new LazyBakedGlyph(glyph);
            }
            if (FontStorage.isAdvanceInvalid(glyph.getMetrics())) continue;
            if (lazyBakedGlyph.glyph == glyph) {
                return new GlyphPair(lazyBakedGlyph, lazyBakedGlyph);
            }
            return new GlyphPair(lazyBakedGlyph, new LazyBakedGlyph(glyph));
        }
        if (lazyBakedGlyph != null) {
            return new GlyphPair(lazyBakedGlyph, this.blankGlyphSupplier);
        }
        return this.blankBakedGlyphPair;
    }

    GlyphPair getBaked(int codePoint) {
        return this.bakedGlyphCache.computeIfAbsent(codePoint, this.findGlyph);
    }

    public BakedGlyph getObfuscatedBakedGlyph(Random random, int width) {
        IntList intList = (IntList)this.charactersByWidth.get(width);
        if (intList != null && !intList.isEmpty()) {
            return this.getBaked(intList.getInt(random.nextInt(intList.size()))).advanceValidating().get();
        }
        return this.blankBakedGlyph;
    }

    public EffectGlyph getRectangleBakedGlyph() {
        return Objects.requireNonNull(this.whiteRectangleBakedGlyph);
    }

    public GlyphProvider getGlyphs(boolean advanceValidating) {
        return advanceValidating ? this.advanceValidatingGlyphs : this.anyGlyphs;
    }

    @Environment(value=EnvType.CLIENT)
    record GlyphPair(Supplier<BakedGlyph> any, Supplier<BakedGlyph> advanceValidating) {
        Supplier<BakedGlyph> get(boolean advanceValidating) {
            return advanceValidating ? this.advanceValidating : this.any;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GlyphPair.class, "any;nonFishy", "any", "advanceValidating"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class Glyphs
    implements GlyphProvider {
        private final boolean advanceValidating;

        public Glyphs(boolean advanceValidating) {
            this.advanceValidating = advanceValidating;
        }

        @Override
        public BakedGlyph get(int codePoint) {
            return FontStorage.this.getBaked(codePoint).get(this.advanceValidating).get();
        }

        @Override
        public BakedGlyph getObfuscated(Random random, int width) {
            return FontStorage.this.getObfuscatedBakedGlyph(random, width);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class LazyBakedGlyph
    implements Supplier<BakedGlyph> {
        final Glyph glyph;
        private @Nullable BakedGlyph baked;

        LazyBakedGlyph(Glyph glyph) {
            this.glyph = glyph;
        }

        @Override
        public BakedGlyph get() {
            if (this.baked == null) {
                this.baked = this.glyph.bake(FontStorage.this.abstractBaker);
            }
            return this.baked;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }
}
