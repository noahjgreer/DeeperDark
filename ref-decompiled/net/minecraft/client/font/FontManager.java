package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontManager implements ResourceReloader, AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_JSON = "fonts.json";
   public static final Identifier MISSING_STORAGE_ID = Identifier.ofVanilla("missing");
   private static final ResourceFinder FINDER = ResourceFinder.json("font");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final FontStorage missingStorage;
   private final List fonts = new ArrayList();
   private final Map fontStorages = new HashMap();
   private final TextureManager textureManager;
   @Nullable
   private volatile FontStorage currentStorage;

   public FontManager(TextureManager manager) {
      this.textureManager = manager;
      this.missingStorage = (FontStorage)Util.make(new FontStorage(manager, MISSING_STORAGE_ID), (fontStorage) -> {
         fontStorage.setFonts(List.of(createEmptyFont()), Set.of());
      });
   }

   private static Font.FontFilterPair createEmptyFont() {
      return new Font.FontFilterPair(new BlankFont(), FontFilterType.FilterMap.NO_FILTER);
   }

   public CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      CompletableFuture var10000 = this.loadIndex(resourceManager, executor);
      Objects.requireNonNull(synchronizer);
      return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((index) -> {
         this.reload(index, Profilers.get());
      }, executor2);
   }

   private CompletableFuture loadIndex(ResourceManager resourceManager, Executor executor) {
      List list = new ArrayList();
      Iterator var4 = FINDER.findAllResources(resourceManager).entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry entry = (Map.Entry)var4.next();
         Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
         list.add(CompletableFuture.supplyAsync(() -> {
            List list = loadFontProviders((List)entry.getValue(), identifier);
            FontEntry fontEntry = new FontEntry(identifier);
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               Pair pair = (Pair)var7.next();
               FontKey fontKey = (FontKey)pair.getFirst();
               FontFilterType.FilterMap filterMap = ((FontLoader.Provider)pair.getSecond()).filter();
               ((FontLoader.Provider)pair.getSecond()).definition().build().ifLeft((loadable) -> {
                  CompletableFuture completableFuture = this.load(fontKey, loadable, resourceManager, executor);
                  fontEntry.addBuilder(fontKey, filterMap, completableFuture);
               }).ifRight((reference) -> {
                  fontEntry.addReferenceBuilder(fontKey, filterMap, reference);
               });
            }

            return fontEntry;
         }, executor));
      }

      return Util.combineSafe(list).thenCompose((entries) -> {
         List list = (List)entries.stream().flatMap(FontEntry::getImmediateProviders).collect(Util.toArrayList());
         Font.FontFilterPair fontFilterPair = createEmptyFont();
         list.add(CompletableFuture.completedFuture(Optional.of(fontFilterPair.provider())));
         return Util.combineSafe(list).thenCompose((providers) -> {
            Map map = this.getRequiredFontProviders(entries);
            CompletableFuture[] completableFutures = (CompletableFuture[])map.values().stream().map((dest) -> {
               return CompletableFuture.runAsync(() -> {
                  this.insertFont(dest, fontFilterPair);
               }, executor);
            }).toArray((i) -> {
               return new CompletableFuture[i];
            });
            return CompletableFuture.allOf(completableFutures).thenApply((ignored) -> {
               List list2 = providers.stream().flatMap(Optional::stream).toList();
               return new ProviderIndex(map, list2);
            });
         });
      });
   }

   private CompletableFuture load(FontKey key, FontLoader.Loadable loadable, ResourceManager resourceManager, Executor executor) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            return Optional.of(loadable.load(resourceManager));
         } catch (Exception var4) {
            LOGGER.warn("Failed to load builder {}, rejecting", key, var4);
            return Optional.empty();
         }
      }, executor);
   }

   private Map getRequiredFontProviders(List entries) {
      Map map = new HashMap();
      DependencyTracker dependencyTracker = new DependencyTracker();
      entries.forEach((entry) -> {
         dependencyTracker.add(entry.fontId, entry);
      });
      dependencyTracker.traverse((dependent, fontEntry) -> {
         Objects.requireNonNull(map);
         fontEntry.getRequiredFontProviders(map::get).ifPresent((fonts) -> {
            map.put(dependent, fonts);
         });
      });
      return map;
   }

   private void insertFont(List fonts, Font.FontFilterPair font) {
      fonts.add(0, font);
      IntSet intSet = new IntOpenHashSet();
      Iterator var4 = fonts.iterator();

      while(var4.hasNext()) {
         Font.FontFilterPair fontFilterPair = (Font.FontFilterPair)var4.next();
         intSet.addAll(fontFilterPair.provider().getProvidedGlyphs());
      }

      intSet.forEach((codePoint) -> {
         if (codePoint != 32) {
            Iterator var2 = Lists.reverse(fonts).iterator();

            while(var2.hasNext()) {
               Font.FontFilterPair fontFilterPair = (Font.FontFilterPair)var2.next();
               if (fontFilterPair.provider().getGlyph(codePoint) != null) {
                  break;
               }
            }

         }
      });
   }

   private static Set getActiveFilters(GameOptions options) {
      Set set = EnumSet.noneOf(FontFilterType.class);
      if ((Boolean)options.getForceUnicodeFont().getValue()) {
         set.add(FontFilterType.UNIFORM);
      }

      if ((Boolean)options.getJapaneseGlyphVariants().getValue()) {
         set.add(FontFilterType.JAPANESE_VARIANTS);
      }

      return set;
   }

   private void reload(ProviderIndex index, Profiler profiler) {
      profiler.push("closing");
      this.currentStorage = null;
      this.fontStorages.values().forEach(FontStorage::close);
      this.fontStorages.clear();
      this.fonts.forEach(Font::close);
      this.fonts.clear();
      Set set = getActiveFilters(MinecraftClient.getInstance().options);
      profiler.swap("reloading");
      index.fontSets().forEach((id, fonts) -> {
         FontStorage fontStorage = new FontStorage(this.textureManager, id);
         fontStorage.setFonts(Lists.reverse(fonts), set);
         this.fontStorages.put(id, fontStorage);
      });
      this.fonts.addAll(index.allProviders);
      profiler.pop();
      if (!this.fontStorages.containsKey(MinecraftClient.DEFAULT_FONT_ID)) {
         throw new IllegalStateException("Default font failed to load");
      }
   }

   public void setActiveFilters(GameOptions options) {
      Set set = getActiveFilters(options);
      Iterator var3 = this.fontStorages.values().iterator();

      while(var3.hasNext()) {
         FontStorage fontStorage = (FontStorage)var3.next();
         fontStorage.setActiveFilters(set);
      }

   }

   private static List loadFontProviders(List fontResources, Identifier id) {
      List list = new ArrayList();
      Iterator var3 = fontResources.iterator();

      while(var3.hasNext()) {
         Resource resource = (Resource)var3.next();

         try {
            Reader reader = resource.getReader();

            try {
               JsonElement jsonElement = (JsonElement)GSON.fromJson(reader, JsonElement.class);
               Providers providers = (Providers)FontManager.Providers.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
               List list2 = providers.providers;

               for(int i = list2.size() - 1; i >= 0; --i) {
                  FontKey fontKey = new FontKey(id, resource.getPackId(), i);
                  list.add(Pair.of(fontKey, (FontLoader.Provider)list2.get(i)));
               }
            } catch (Throwable var12) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (Exception var13) {
            LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{id, "fonts.json", resource.getPackId(), var13});
         }
      }

      return list;
   }

   public TextRenderer createTextRenderer() {
      return new TextRenderer(this::getStorage, false);
   }

   public TextRenderer createAdvanceValidatingTextRenderer() {
      return new TextRenderer(this::getStorage, true);
   }

   private FontStorage getStorageInternal(Identifier id) {
      return (FontStorage)this.fontStorages.getOrDefault(id, this.missingStorage);
   }

   private FontStorage getStorage(Identifier id) {
      FontStorage fontStorage = this.currentStorage;
      if (fontStorage != null && id.equals(fontStorage.getId())) {
         return fontStorage;
      } else {
         FontStorage fontStorage2 = this.getStorageInternal(id);
         this.currentStorage = fontStorage2;
         return fontStorage2;
      }
   }

   public void close() {
      this.fontStorages.values().forEach(FontStorage::close);
      this.fonts.forEach(Font::close);
      this.missingStorage.close();
   }

   @Environment(EnvType.CLIENT)
   private static record FontKey(Identifier fontId, String pack, int index) {
      FontKey(Identifier identifier, String string, int i) {
         this.fontId = identifier;
         this.pack = string;
         this.index = i;
      }

      public String toString() {
         String var10000 = String.valueOf(this.fontId);
         return "(" + var10000 + ": builder #" + this.index + " from pack " + this.pack + ")";
      }

      public Identifier fontId() {
         return this.fontId;
      }

      public String pack() {
         return this.pack;
      }

      public int index() {
         return this.index;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record ProviderIndex(Map fontSets, List allProviders) {
      final List allProviders;

      ProviderIndex(Map map, List list) {
         this.fontSets = map;
         this.allProviders = list;
      }

      public Map fontSets() {
         return this.fontSets;
      }

      public List allProviders() {
         return this.allProviders;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Providers(List providers) {
      final List providers;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(FontLoader.Provider.CODEC.listOf().fieldOf("providers").forGetter(Providers::providers)).apply(instance, Providers::new);
      });

      private Providers(List list) {
         this.providers = list;
      }

      public List providers() {
         return this.providers;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record FontEntry(Identifier fontId, List builders, Set dependencies) implements DependencyTracker.Dependencies {
      final Identifier fontId;

      public FontEntry(Identifier fontId) {
         this(fontId, new ArrayList(), new HashSet());
      }

      private FontEntry(Identifier identifier, List list, Set set) {
         this.fontId = identifier;
         this.builders = list;
         this.dependencies = set;
      }

      public void addReferenceBuilder(FontKey key, FontFilterType.FilterMap filters, FontLoader.Reference reference) {
         this.builders.add(new Builder(key, filters, Either.right(reference.id())));
         this.dependencies.add(reference.id());
      }

      public void addBuilder(FontKey key, FontFilterType.FilterMap filters, CompletableFuture fontFuture) {
         this.builders.add(new Builder(key, filters, Either.left(fontFuture)));
      }

      private Stream getImmediateProviders() {
         return this.builders.stream().flatMap((builder) -> {
            return builder.result.left().stream();
         });
      }

      public Optional getRequiredFontProviders(Function fontRetriever) {
         List list = new ArrayList();
         Iterator var3 = this.builders.iterator();

         while(var3.hasNext()) {
            Builder builder = (Builder)var3.next();
            Optional optional = builder.build(fontRetriever);
            if (!optional.isPresent()) {
               return Optional.empty();
            }

            list.addAll((Collection)optional.get());
         }

         return Optional.of(list);
      }

      public void forDependencies(Consumer callback) {
         this.dependencies.forEach(callback);
      }

      public void forOptionalDependencies(Consumer callback) {
      }

      public Identifier fontId() {
         return this.fontId;
      }

      public List builders() {
         return this.builders;
      }

      public Set dependencies() {
         return this.dependencies;
      }
   }

   @Environment(EnvType.CLIENT)
   static record Builder(FontKey id, FontFilterType.FilterMap filter, Either result) {
      final Either result;

      Builder(FontKey fontKey, FontFilterType.FilterMap filterMap, Either either) {
         this.id = fontKey;
         this.filter = filterMap;
         this.result = either;
      }

      public Optional build(Function fontRetriever) {
         return (Optional)this.result.map((future) -> {
            return ((Optional)future.join()).map((font) -> {
               return List.of(new Font.FontFilterPair(font, this.filter));
            });
         }, (referee) -> {
            List list = (List)fontRetriever.apply(referee);
            if (list == null) {
               FontManager.LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, this.id);
               return Optional.empty();
            } else {
               return Optional.of(list.stream().map(this::applyFilter).toList());
            }
         });
      }

      private Font.FontFilterPair applyFilter(Font.FontFilterPair font) {
         return new Font.FontFilterPair(font.provider(), this.filter.apply(font.filter()));
      }

      public FontKey id() {
         return this.id;
      }

      public FontFilterType.FilterMap filter() {
         return this.filter;
      }

      public Either result() {
         return this.result;
      }
   }
}
