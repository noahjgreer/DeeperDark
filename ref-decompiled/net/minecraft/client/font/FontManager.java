/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.BlankFont
 *  net.minecraft.client.font.Font
 *  net.minecraft.client.font.Font$FontFilterPair
 *  net.minecraft.client.font.FontFilterType
 *  net.minecraft.client.font.FontFilterType$FilterMap
 *  net.minecraft.client.font.FontLoader$Loadable
 *  net.minecraft.client.font.FontLoader$Provider
 *  net.minecraft.client.font.FontManager
 *  net.minecraft.client.font.FontManager$FontEntry
 *  net.minecraft.client.font.FontManager$FontKey
 *  net.minecraft.client.font.FontManager$Fonts
 *  net.minecraft.client.font.FontManager$ProviderIndex
 *  net.minecraft.client.font.FontManager$Providers
 *  net.minecraft.client.font.FontStorage
 *  net.minecraft.client.font.GlyphBaker
 *  net.minecraft.client.font.GlyphProvider
 *  net.minecraft.client.font.PlayerHeadGlyphs
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$GlyphsProvider
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.client.texture.SpriteAtlasGlyphs
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.resource.DependencyTracker
 *  net.minecraft.resource.DependencyTracker$Dependencies
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.ResourceReloader
 *  net.minecraft.resource.ResourceReloader$Store
 *  net.minecraft.resource.ResourceReloader$Synchronizer
 *  net.minecraft.text.StyleSpriteSource$Sprite
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.Profilers
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphBaker;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.PlayerHeadGlyphs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteAtlasGlyphs;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FontManager
implements ResourceReloader,
AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_JSON = "fonts.json";
    public static final Identifier MISSING_STORAGE_ID = Identifier.ofVanilla((String)"missing");
    private static final ResourceFinder FINDER = ResourceFinder.json((String)"font");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    final FontStorage missingStorage;
    private final List<Font> fonts = new ArrayList();
    private final Map<Identifier, FontStorage> fontStorages = new HashMap();
    private final TextureManager textureManager;
    private final Fonts anyFonts = new Fonts(this, false);
    private final Fonts advanceValidatedFonts = new Fonts(this, true);
    private final AtlasManager atlasManager;
    private final Map<Identifier, SpriteAtlasGlyphs> spriteGlyphs = new HashMap();
    final PlayerHeadGlyphs playerHeadGlyphs;

    public FontManager(TextureManager textureManager, AtlasManager atlasManager, PlayerSkinCache playerSkinCache) {
        this.textureManager = textureManager;
        this.atlasManager = atlasManager;
        this.missingStorage = this.createFontStorage(MISSING_STORAGE_ID, List.of(FontManager.createEmptyFont()), Set.of());
        this.playerHeadGlyphs = new PlayerHeadGlyphs(playerSkinCache);
    }

    private FontStorage createFontStorage(Identifier fontId, List<Font.FontFilterPair> allFonts, Set<FontFilterType> filters) {
        GlyphBaker glyphBaker = new GlyphBaker(this.textureManager, fontId);
        FontStorage fontStorage = new FontStorage(glyphBaker);
        fontStorage.setFonts(allFonts, filters);
        return fontStorage;
    }

    private static Font.FontFilterPair createEmptyFont() {
        return new Font.FontFilterPair((Font)new BlankFont(), FontFilterType.FilterMap.NO_FILTER);
    }

    public CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        return ((CompletableFuture)this.loadIndex(store.getResourceManager(), executor).thenCompose(arg_0 -> ((ResourceReloader.Synchronizer)synchronizer).whenPrepared(arg_0))).thenAcceptAsync(index -> this.reload(index, Profilers.get()), executor2);
    }

    private CompletableFuture<ProviderIndex> loadIndex(ResourceManager resourceManager, Executor executor) {
        ArrayList<CompletableFuture<FontEntry>> list = new ArrayList<CompletableFuture<FontEntry>>();
        for (Map.Entry entry : FINDER.findAllResources(resourceManager).entrySet()) {
            Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
            list.add(CompletableFuture.supplyAsync(() -> {
                List list = FontManager.loadFontProviders((List)((List)entry.getValue()), (Identifier)identifier);
                FontEntry fontEntry = new FontEntry(identifier);
                for (Pair pair : list) {
                    FontKey fontKey = (FontKey)pair.getFirst();
                    FontFilterType.FilterMap filterMap = ((FontLoader.Provider)pair.getSecond()).filter();
                    ((FontLoader.Provider)pair.getSecond()).definition().build().ifLeft(loadable -> {
                        CompletableFuture completableFuture = this.load(fontKey, loadable, resourceManager, executor);
                        fontEntry.addBuilder(fontKey, filterMap, completableFuture);
                    }).ifRight(reference -> fontEntry.addReferenceBuilder(fontKey, filterMap, reference));
                }
                return fontEntry;
            }, executor));
        }
        return Util.combineSafe(list).thenCompose(entries -> {
            List list = (List)entries.stream().flatMap(FontEntry::getImmediateProviders).collect(Util.toArrayList());
            Font.FontFilterPair fontFilterPair = FontManager.createEmptyFont();
            list.add(CompletableFuture.completedFuture(Optional.of(fontFilterPair.provider())));
            return Util.combineSafe((List)list).thenCompose(providers -> {
                Map map = this.getRequiredFontProviders(entries);
                CompletableFuture[] completableFutures = (CompletableFuture[])map.values().stream().map(dest -> CompletableFuture.runAsync(() -> this.insertFont(dest, fontFilterPair), executor)).toArray(CompletableFuture[]::new);
                return CompletableFuture.allOf(completableFutures).thenApply(ignored -> {
                    List list2 = providers.stream().flatMap(Optional::stream).toList();
                    return new ProviderIndex(map, list2);
                });
            });
        });
    }

    private CompletableFuture<Optional<Font>> load(FontKey key, FontLoader.Loadable loadable, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(loadable.load(resourceManager));
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load builder {}, rejecting", (Object)key, (Object)exception);
                return Optional.empty();
            }
        }, executor);
    }

    private Map<Identifier, List<Font.FontFilterPair>> getRequiredFontProviders(List<FontEntry> entries) {
        HashMap<Identifier, List<Font.FontFilterPair>> map = new HashMap<Identifier, List<Font.FontFilterPair>>();
        DependencyTracker dependencyTracker = new DependencyTracker();
        entries.forEach(entry -> dependencyTracker.add((Object)entry.fontId, (DependencyTracker.Dependencies)entry));
        dependencyTracker.traverse((dependent, fontEntry) -> fontEntry.getRequiredFontProviders(map::get).ifPresent(fonts -> map.put((Identifier)dependent, (List<Font.FontFilterPair>)fonts)));
        return map;
    }

    private void insertFont(List<Font.FontFilterPair> fonts, Font.FontFilterPair font) {
        fonts.add(0, font);
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (Font.FontFilterPair fontFilterPair : fonts) {
            intSet.addAll((IntCollection)fontFilterPair.provider().getProvidedGlyphs());
        }
        intSet.forEach(codePoint -> {
            Font.FontFilterPair fontFilterPair;
            if (codePoint == 32) {
                return;
            }
            Iterator iterator = Lists.reverse((List)fonts).iterator();
            while (iterator.hasNext() && (fontFilterPair = (Font.FontFilterPair)iterator.next()).provider().getGlyph(codePoint) == null) {
            }
        });
    }

    private static Set<FontFilterType> getActiveFilters(GameOptions options) {
        EnumSet<FontFilterType> set = EnumSet.noneOf(FontFilterType.class);
        if (((Boolean)options.getForceUnicodeFont().getValue()).booleanValue()) {
            set.add(FontFilterType.UNIFORM);
        }
        if (((Boolean)options.getJapaneseGlyphVariants().getValue()).booleanValue()) {
            set.add(FontFilterType.JAPANESE_VARIANTS);
        }
        return set;
    }

    private void reload(ProviderIndex index, Profiler profiler) {
        profiler.push("closing");
        this.anyFonts.clear();
        this.advanceValidatedFonts.clear();
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        Set set = FontManager.getActiveFilters((GameOptions)MinecraftClient.getInstance().options);
        profiler.swap("reloading");
        index.fontSets().forEach((id, fonts) -> this.fontStorages.put(id, this.createFontStorage(id, Lists.reverse((List)fonts), set)));
        this.fonts.addAll(index.allProviders);
        profiler.pop();
        if (!this.fontStorages.containsKey(MinecraftClient.DEFAULT_FONT_ID)) {
            throw new IllegalStateException("Default font failed to load");
        }
        this.spriteGlyphs.clear();
        this.atlasManager.acceptAtlasTextures((definitionId, atlasTexture) -> this.spriteGlyphs.put(definitionId, new SpriteAtlasGlyphs(atlasTexture)));
    }

    public void setActiveFilters(GameOptions options) {
        Set set = FontManager.getActiveFilters((GameOptions)options);
        for (FontStorage fontStorage : this.fontStorages.values()) {
            fontStorage.setActiveFilters(set);
        }
    }

    private static List<Pair<FontKey, FontLoader.Provider>> loadFontProviders(List<Resource> fontResources, Identifier id) {
        ArrayList<Pair<FontKey, FontLoader.Provider>> list = new ArrayList<Pair<FontKey, FontLoader.Provider>>();
        for (Resource resource : fontResources) {
            try {
                BufferedReader reader = resource.getReader();
                try {
                    JsonElement jsonElement = (JsonElement)GSON.fromJson((Reader)reader, JsonElement.class);
                    Providers providers = (Providers)Providers.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonParseException::new);
                    List list2 = providers.providers;
                    for (int i = list2.size() - 1; i >= 0; --i) {
                        FontKey fontKey = new FontKey(id, resource.getPackId(), i);
                        list.add((Pair<FontKey, FontLoader.Provider>)Pair.of((Object)fontKey, (Object)((FontLoader.Provider)list2.get(i))));
                    }
                }
                finally {
                    if (reader == null) continue;
                    ((Reader)reader).close();
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{id, "fonts.json", resource.getPackId(), exception});
            }
        }
        return list;
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer((TextRenderer.GlyphsProvider)this.anyFonts);
    }

    public TextRenderer createAdvanceValidatingTextRenderer() {
        return new TextRenderer((TextRenderer.GlyphsProvider)this.advanceValidatedFonts);
    }

    FontStorage getStorageInternal(Identifier id) {
        return this.fontStorages.getOrDefault(id, this.missingStorage);
    }

    GlyphProvider getSpriteGlyphs(StyleSpriteSource.Sprite description) {
        SpriteAtlasGlyphs spriteAtlasGlyphs = (SpriteAtlasGlyphs)this.spriteGlyphs.get(description.atlasId());
        if (spriteAtlasGlyphs == null) {
            return this.missingStorage.getGlyphs(false);
        }
        return spriteAtlasGlyphs.getGlyphProvider(description.spriteId());
    }

    @Override
    public void close() {
        this.anyFonts.close();
        this.advanceValidatedFonts.close();
        this.fontStorages.values().forEach(FontStorage::close);
        this.fonts.forEach(Font::close);
        this.missingStorage.close();
    }
}

