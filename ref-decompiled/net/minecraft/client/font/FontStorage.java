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
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.BuiltinEmptyGlyph
 *  net.minecraft.client.font.EffectGlyph
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Font$FontFilterPair
 *  net.minecraft.client.font.FontFilterType
 *  net.minecraft.client.font.FontStorage
 *  net.minecraft.client.font.FontStorage$GlyphPair
 *  net.minecraft.client.font.FontStorage$Glyphs
 *  net.minecraft.client.font.FontStorage$LazyBakedGlyph
 *  net.minecraft.client.font.Glyph
 *  net.minecraft.client.font.Glyph$AbstractGlyphBaker
 *  net.minecraft.client.font.GlyphBaker
 *  net.minecraft.client.font.GlyphContainer
 *  net.minecraft.client.font.GlyphMetrics
 *  net.minecraft.client.font.GlyphProvider
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphBaker;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final float MAX_ADVANCE = 32.0f;
    private static final BakedGlyph MISSING_GLYPH = new /* Unavailable Anonymous Inner Class!! */;
    final GlyphBaker glyphBaker;
    final Glyph.AbstractGlyphBaker abstractBaker = new /* Unavailable Anonymous Inner Class!! */;
    private List<Font.FontFilterPair> allFonts = List.of();
    private List<Font> availableFonts = List.of();
    private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap();
    private final GlyphContainer<GlyphPair> bakedGlyphCache = new GlyphContainer(GlyphPair[]::new, rowCount -> new GlyphPair[rowCount][]);
    private final IntFunction<GlyphPair> findGlyph = arg_0 -> this.findGlyph(arg_0);
    BakedGlyph blankBakedGlyph = MISSING_GLYPH;
    private final Supplier<BakedGlyph> blankGlyphSupplier = () -> this.blankBakedGlyph;
    private final GlyphPair blankBakedGlyphPair = new GlyphPair(this.blankGlyphSupplier, this.blankGlyphSupplier);
    private @Nullable EffectGlyph whiteRectangleBakedGlyph;
    private final GlyphProvider anyGlyphs = new Glyphs(this, false);
    private final GlyphProvider advanceValidatingGlyphs = new Glyphs(this, true);

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
        this.blankBakedGlyph = (BakedGlyph)Objects.requireNonNull(BuiltinEmptyGlyph.MISSING.bake(this.glyphBaker));
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
                ((IntList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil((float)glyph.getMetrics().getAdvance(false)), i -> new IntArrayList())).add(codePoint);
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
                lazyBakedGlyph = new LazyBakedGlyph(this, glyph);
            }
            if (FontStorage.isAdvanceInvalid((GlyphMetrics)glyph.getMetrics())) continue;
            if (lazyBakedGlyph.glyph == glyph) {
                return new GlyphPair((Supplier)lazyBakedGlyph, (Supplier)lazyBakedGlyph);
            }
            return new GlyphPair((Supplier)lazyBakedGlyph, (Supplier)new LazyBakedGlyph(this, glyph));
        }
        if (lazyBakedGlyph != null) {
            return new GlyphPair(lazyBakedGlyph, this.blankGlyphSupplier);
        }
        return this.blankBakedGlyphPair;
    }

    GlyphPair getBaked(int codePoint) {
        return (GlyphPair)this.bakedGlyphCache.computeIfAbsent(codePoint, this.findGlyph);
    }

    public BakedGlyph getObfuscatedBakedGlyph(Random random, int width) {
        IntList intList = (IntList)this.charactersByWidth.get(width);
        if (intList != null && !intList.isEmpty()) {
            return (BakedGlyph)this.getBaked(intList.getInt(random.nextInt(intList.size()))).advanceValidating().get();
        }
        return this.blankBakedGlyph;
    }

    public EffectGlyph getRectangleBakedGlyph() {
        return Objects.requireNonNull(this.whiteRectangleBakedGlyph);
    }

    public GlyphProvider getGlyphs(boolean advanceValidating) {
        return advanceValidating ? this.advanceValidatingGlyphs : this.anyGlyphs;
    }
}

