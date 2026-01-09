package net.minecraft.registry.tag;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagGroupLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   final EntrySupplier entrySupplier;
   private final String dataType;

   public TagGroupLoader(EntrySupplier entrySupplier, String dataType) {
      this.entrySupplier = entrySupplier;
      this.dataType = dataType;
   }

   public Map loadTags(ResourceManager resourceManager) {
      Map map = new HashMap();
      ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);
      Iterator var4 = resourceFinder.findAllResources(resourceManager).entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry entry = (Map.Entry)var4.next();
         Identifier identifier = (Identifier)entry.getKey();
         Identifier identifier2 = resourceFinder.toResourceId(identifier);
         Iterator var8 = ((List)entry.getValue()).iterator();

         while(var8.hasNext()) {
            Resource resource = (Resource)var8.next();

            try {
               Reader reader = resource.getReader();

               try {
                  JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
                  List list = (List)map.computeIfAbsent(identifier2, (id) -> {
                     return new ArrayList();
                  });
                  TagFile tagFile = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, jsonElement)).getOrThrow();
                  if (tagFile.replace()) {
                     list.clear();
                  }

                  String string = resource.getPackId();
                  tagFile.entries().forEach((entryx) -> {
                     list.add(new TrackedEntry(entryx, string));
                  });
               } catch (Throwable var16) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (reader != null) {
                  reader.close();
               }
            } catch (Exception var17) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{identifier2, identifier, resource.getPackId(), var17});
            }
         }
      }

      return map;
   }

   private Either resolveAll(TagEntry.ValueGetter valueGetter, List entries) {
      SequencedSet sequencedSet = new LinkedHashSet();
      List list = new ArrayList();
      Iterator var5 = entries.iterator();

      while(var5.hasNext()) {
         TrackedEntry trackedEntry = (TrackedEntry)var5.next();
         TagEntry var10000 = trackedEntry.entry();
         Objects.requireNonNull(sequencedSet);
         if (!var10000.resolve(valueGetter, sequencedSet::add)) {
            list.add(trackedEntry);
         }
      }

      return list.isEmpty() ? Either.right(List.copyOf(sequencedSet)) : Either.left(list);
   }

   public Map buildGroup(Map tags) {
      final Map map = new HashMap();
      TagEntry.ValueGetter valueGetter = new TagEntry.ValueGetter() {
         @Nullable
         public Object direct(Identifier id, boolean required) {
            return TagGroupLoader.this.entrySupplier.get(id, required).orElse((Object)null);
         }

         @Nullable
         public Collection tag(Identifier id) {
            return (Collection)map.get(id);
         }
      };
      DependencyTracker dependencyTracker = new DependencyTracker();
      tags.forEach((id, entries) -> {
         dependencyTracker.add(id, new TagDependencies(entries));
      });
      dependencyTracker.traverse((id, dependencies) -> {
         this.resolveAll(valueGetter, dependencies.entries).ifLeft((missingReferences) -> {
            LOGGER.error("Couldn't load tag {} as it is missing following references: {}", id, missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", ")));
         }).ifRight((values) -> {
            map.put(id, values);
         });
      });
      return map;
   }

   public static void loadFromNetwork(TagPacketSerializer.Serialized tags, MutableRegistry registry) {
      Map var10000 = tags.toRegistryTags(registry).tags;
      Objects.requireNonNull(registry);
      var10000.forEach(registry::setEntries);
   }

   public static List startReload(ResourceManager resourceManager, DynamicRegistryManager registryManager) {
      return (List)registryManager.streamAllRegistries().map((registry) -> {
         return startReload(resourceManager, registry.value());
      }).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static void loadInitial(ResourceManager resourceManager, MutableRegistry registry) {
      RegistryKey registryKey = registry.getKey();
      TagGroupLoader tagGroupLoader = new TagGroupLoader(TagGroupLoader.EntrySupplier.forInitial(registry), RegistryKeys.getTagPath(registryKey));
      tagGroupLoader.buildGroup(tagGroupLoader.loadTags(resourceManager)).forEach((id, entries) -> {
         registry.setEntries(TagKey.of(registryKey, id), entries);
      });
   }

   private static Map toTagKeyedMap(RegistryKey registryRef, Map tags) {
      return (Map)tags.entrySet().stream().collect(Collectors.toUnmodifiableMap((entry) -> {
         return TagKey.of(registryRef, (Identifier)entry.getKey());
      }, Map.Entry::getValue));
   }

   private static Optional startReload(ResourceManager resourceManager, Registry registry) {
      RegistryKey registryKey = registry.getKey();
      TagGroupLoader tagGroupLoader = new TagGroupLoader(TagGroupLoader.EntrySupplier.forReload(registry), RegistryKeys.getTagPath(registryKey));
      RegistryTags registryTags = new RegistryTags(registryKey, toTagKeyedMap(registry.getKey(), tagGroupLoader.buildGroup(tagGroupLoader.loadTags(resourceManager))));
      return registryTags.tags().isEmpty() ? Optional.empty() : Optional.of(registry.startTagReload(registryTags));
   }

   public static List collectRegistries(DynamicRegistryManager.Immutable registryManager, List tagLoads) {
      List list = new ArrayList();
      registryManager.streamAllRegistries().forEach((registry) -> {
         Registry.PendingTagLoad pendingTagLoad = find(tagLoads, registry.key());
         list.add(pendingTagLoad != null ? pendingTagLoad.getLookup() : registry.value());
      });
      return list;
   }

   @Nullable
   private static Registry.PendingTagLoad find(List pendingTags, RegistryKey registryRef) {
      Iterator var2 = pendingTags.iterator();

      Registry.PendingTagLoad pendingTagLoad;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         pendingTagLoad = (Registry.PendingTagLoad)var2.next();
      } while(pendingTagLoad.getKey() != registryRef);

      return pendingTagLoad;
   }

   public interface EntrySupplier {
      Optional get(Identifier id, boolean required);

      static EntrySupplier forReload(Registry registry) {
         return (id, required) -> {
            return registry.getEntry(id);
         };
      }

      static EntrySupplier forInitial(MutableRegistry registry) {
         RegistryEntryLookup registryEntryLookup = registry.createMutableRegistryLookup();
         return (id, required) -> {
            return ((RegistryEntryLookup)(required ? registryEntryLookup : registry)).getOptional(RegistryKey.of(registry.getKey(), id));
         };
      }
   }

   public static record TrackedEntry(TagEntry entry, String source) {
      final TagEntry entry;

      public TrackedEntry(TagEntry tagEntry, String source) {
         this.entry = tagEntry;
         this.source = source;
      }

      public String toString() {
         String var10000 = String.valueOf(this.entry);
         return var10000 + " (from " + this.source + ")";
      }

      public TagEntry entry() {
         return this.entry;
      }

      public String source() {
         return this.source;
      }
   }

   public static record RegistryTags(RegistryKey key, Map tags) {
      final Map tags;

      public RegistryTags(RegistryKey registryKey, Map map) {
         this.key = registryKey;
         this.tags = map;
      }

      public RegistryKey key() {
         return this.key;
      }

      public Map tags() {
         return this.tags;
      }
   }

   static record TagDependencies(List entries) implements DependencyTracker.Dependencies {
      final List entries;

      TagDependencies(List list) {
         this.entries = list;
      }

      public void forDependencies(Consumer callback) {
         this.entries.forEach((entry) -> {
            entry.entry.forEachRequiredTagId(callback);
         });
      }

      public void forOptionalDependencies(Consumer callback) {
         this.entries.forEach((entry) -> {
            entry.entry.forEachOptionalTagId(callback);
         });
      }

      public List entries() {
         return this.entries;
      }
   }
}
