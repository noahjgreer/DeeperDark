package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class SimpleRegistry implements MutableRegistry {
   private final RegistryKey key;
   private final ObjectList rawIdToEntry;
   private final Reference2IntMap entryToRawId;
   private final Map idToEntry;
   private final Map keyToEntry;
   private final Map valueToEntry;
   private final Map keyToEntryInfo;
   private Lifecycle lifecycle;
   private final Map tags;
   TagLookup tagLookup;
   private boolean frozen;
   @Nullable
   private Map intrusiveValueToEntry;

   public Stream getTags() {
      return this.streamTags();
   }

   public SimpleRegistry(RegistryKey key, Lifecycle lifecycle) {
      this(key, lifecycle, false);
   }

   public SimpleRegistry(RegistryKey key, Lifecycle lifecycle, boolean intrusive) {
      this.rawIdToEntry = new ObjectArrayList(256);
      this.entryToRawId = (Reference2IntMap)Util.make(new Reference2IntOpenHashMap(), (map) -> {
         map.defaultReturnValue(-1);
      });
      this.idToEntry = new HashMap();
      this.keyToEntry = new HashMap();
      this.valueToEntry = new IdentityHashMap();
      this.keyToEntryInfo = new IdentityHashMap();
      this.tags = new IdentityHashMap();
      this.tagLookup = SimpleRegistry.TagLookup.ofUnbound();
      this.key = key;
      this.lifecycle = lifecycle;
      if (intrusive) {
         this.intrusiveValueToEntry = new IdentityHashMap();
      }

   }

   public RegistryKey getKey() {
      return this.key;
   }

   public String toString() {
      String var10000 = String.valueOf(this.key);
      return "Registry[" + var10000 + " (" + String.valueOf(this.lifecycle) + ")]";
   }

   private void assertNotFrozen() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void assertNotFrozen(RegistryKey key) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + String.valueOf(key) + ")");
      }
   }

   public RegistryEntry.Reference add(RegistryKey key, Object value, RegistryEntryInfo info) {
      this.assertNotFrozen(key);
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      if (this.idToEntry.containsKey(key.getValue())) {
         throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("Adding duplicate key '" + String.valueOf(key) + "' to registry"));
      } else if (this.valueToEntry.containsKey(value)) {
         throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("Adding duplicate value '" + String.valueOf(value) + "' to registry"));
      } else {
         RegistryEntry.Reference reference;
         if (this.intrusiveValueToEntry != null) {
            reference = (RegistryEntry.Reference)this.intrusiveValueToEntry.remove(value);
            if (reference == null) {
               String var10002 = String.valueOf(key);
               throw new AssertionError("Missing intrusive holder for " + var10002 + ":" + String.valueOf(value));
            }

            reference.setRegistryKey(key);
         } else {
            reference = (RegistryEntry.Reference)this.keyToEntry.computeIfAbsent(key, (k) -> {
               return RegistryEntry.Reference.standAlone(this, k);
            });
         }

         this.keyToEntry.put(key, reference);
         this.idToEntry.put(key.getValue(), reference);
         this.valueToEntry.put(value, reference);
         int i = this.rawIdToEntry.size();
         this.rawIdToEntry.add(reference);
         this.entryToRawId.put(value, i);
         this.keyToEntryInfo.put(key, info);
         this.lifecycle = this.lifecycle.add(info.lifecycle());
         return reference;
      }
   }

   @Nullable
   public Identifier getId(Object value) {
      RegistryEntry.Reference reference = (RegistryEntry.Reference)this.valueToEntry.get(value);
      return reference != null ? reference.registryKey().getValue() : null;
   }

   public Optional getKey(Object entry) {
      return Optional.ofNullable((RegistryEntry.Reference)this.valueToEntry.get(entry)).map(RegistryEntry.Reference::registryKey);
   }

   public int getRawId(@Nullable Object value) {
      return this.entryToRawId.getInt(value);
   }

   @Nullable
   public Object get(@Nullable RegistryKey key) {
      return getValue((RegistryEntry.Reference)this.keyToEntry.get(key));
   }

   @Nullable
   public Object get(int index) {
      return index >= 0 && index < this.rawIdToEntry.size() ? ((RegistryEntry.Reference)this.rawIdToEntry.get(index)).value() : null;
   }

   public Optional getEntry(int rawId) {
      return rawId >= 0 && rawId < this.rawIdToEntry.size() ? Optional.ofNullable((RegistryEntry.Reference)this.rawIdToEntry.get(rawId)) : Optional.empty();
   }

   public Optional getEntry(Identifier id) {
      return Optional.ofNullable((RegistryEntry.Reference)this.idToEntry.get(id));
   }

   public Optional getOptional(RegistryKey key) {
      return Optional.ofNullable((RegistryEntry.Reference)this.keyToEntry.get(key));
   }

   public Optional getDefaultEntry() {
      return this.rawIdToEntry.isEmpty() ? Optional.empty() : Optional.of((RegistryEntry.Reference)this.rawIdToEntry.getFirst());
   }

   public RegistryEntry getEntry(Object value) {
      RegistryEntry.Reference reference = (RegistryEntry.Reference)this.valueToEntry.get(value);
      return (RegistryEntry)(reference != null ? reference : RegistryEntry.of(value));
   }

   RegistryEntry.Reference getOrCreateEntry(RegistryKey key) {
      return (RegistryEntry.Reference)this.keyToEntry.computeIfAbsent(key, (key2) -> {
         if (this.intrusiveValueToEntry != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.assertNotFrozen(key2);
            return RegistryEntry.Reference.standAlone(this, key2);
         }
      });
   }

   public int size() {
      return this.keyToEntry.size();
   }

   public Optional getEntryInfo(RegistryKey key) {
      return Optional.ofNullable((RegistryEntryInfo)this.keyToEntryInfo.get(key));
   }

   public Lifecycle getLifecycle() {
      return this.lifecycle;
   }

   public Iterator iterator() {
      return Iterators.transform(this.rawIdToEntry.iterator(), RegistryEntry::value);
   }

   @Nullable
   public Object get(@Nullable Identifier id) {
      RegistryEntry.Reference reference = (RegistryEntry.Reference)this.idToEntry.get(id);
      return getValue(reference);
   }

   @Nullable
   private static Object getValue(@Nullable RegistryEntry.Reference entry) {
      return entry != null ? entry.value() : null;
   }

   public Set getIds() {
      return Collections.unmodifiableSet(this.idToEntry.keySet());
   }

   public Set getKeys() {
      return Collections.unmodifiableSet(this.keyToEntry.keySet());
   }

   public Set getEntrySet() {
      return Collections.unmodifiableSet(Util.transformMapValuesLazy(this.keyToEntry, RegistryEntry::value).entrySet());
   }

   public Stream streamEntries() {
      return this.rawIdToEntry.stream();
   }

   public Stream streamTags() {
      return this.tagLookup.stream();
   }

   RegistryEntryList.Named getTag(TagKey key) {
      return (RegistryEntryList.Named)this.tags.computeIfAbsent(key, this::createNamedEntryList);
   }

   private RegistryEntryList.Named createNamedEntryList(TagKey tag) {
      return new RegistryEntryList.Named(this, tag);
   }

   public boolean isEmpty() {
      return this.keyToEntry.isEmpty();
   }

   public Optional getRandom(Random random) {
      return Util.getRandomOrEmpty(this.rawIdToEntry, random);
   }

   public boolean containsId(Identifier id) {
      return this.idToEntry.containsKey(id);
   }

   public boolean contains(RegistryKey key) {
      return this.keyToEntry.containsKey(key);
   }

   public Registry freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.valueToEntry.forEach((value, entry) -> {
            entry.setValue(value);
         });
         List list = this.keyToEntry.entrySet().stream().filter((entry) -> {
            return !((RegistryEntry.Reference)entry.getValue()).hasKeyAndValue();
         }).map((entry) -> {
            return ((RegistryKey)entry.getKey()).getValue();
         }).sorted().toList();
         String var10002;
         if (!list.isEmpty()) {
            var10002 = String.valueOf(this.getKey());
            throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + String.valueOf(list));
         } else {
            if (this.intrusiveValueToEntry != null) {
               if (!this.intrusiveValueToEntry.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + String.valueOf(this.intrusiveValueToEntry.values()));
               }

               this.intrusiveValueToEntry = null;
            }

            if (this.tagLookup.isBound()) {
               throw new IllegalStateException("Tags already present before freezing");
            } else {
               List list2 = this.tags.entrySet().stream().filter((entry) -> {
                  return !((RegistryEntryList.Named)entry.getValue()).isBound();
               }).map((entry) -> {
                  return ((TagKey)entry.getKey()).id();
               }).sorted().toList();
               if (!list2.isEmpty()) {
                  var10002 = String.valueOf(this.getKey());
                  throw new IllegalStateException("Unbound tags in registry " + var10002 + ": " + String.valueOf(list2));
               } else {
                  this.tagLookup = SimpleRegistry.TagLookup.fromMap(this.tags);
                  this.refreshTags();
                  return this;
               }
            }
         }
      }
   }

   public RegistryEntry.Reference createEntry(Object value) {
      if (this.intrusiveValueToEntry == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.assertNotFrozen();
         return (RegistryEntry.Reference)this.intrusiveValueToEntry.computeIfAbsent(value, (valuex) -> {
            return RegistryEntry.Reference.intrusive(this, valuex);
         });
      }
   }

   public Optional getOptional(TagKey tag) {
      return this.tagLookup.getOptional(tag);
   }

   private RegistryEntry.Reference ensureTagable(TagKey key, RegistryEntry entry) {
      String var10002;
      if (!entry.ownerEquals(this)) {
         var10002 = String.valueOf(key);
         throw new IllegalStateException("Can't create named set " + var10002 + " containing value " + String.valueOf(entry) + " from outside registry " + String.valueOf(this));
      } else if (entry instanceof RegistryEntry.Reference) {
         RegistryEntry.Reference reference = (RegistryEntry.Reference)entry;
         return reference;
      } else {
         var10002 = String.valueOf(entry);
         throw new IllegalStateException("Found direct holder " + var10002 + " value in tag " + String.valueOf(key));
      }
   }

   public void setEntries(TagKey tag, List entries) {
      this.assertNotFrozen();
      this.getTag(tag).setEntries(entries);
   }

   void refreshTags() {
      Map map = new IdentityHashMap();
      this.keyToEntry.values().forEach((key) -> {
         map.put(key, new ArrayList());
      });
      this.tagLookup.forEach((key, value) -> {
         Iterator var4 = value.iterator();

         while(var4.hasNext()) {
            RegistryEntry registryEntry = (RegistryEntry)var4.next();
            RegistryEntry.Reference reference = this.ensureTagable(key, registryEntry);
            ((List)map.get(reference)).add(key);
         }

      });
      map.forEach(RegistryEntry.Reference::setTags);
   }

   public void resetTagEntries() {
      this.assertNotFrozen();
      this.tags.values().forEach((tag) -> {
         tag.setEntries(List.of());
      });
   }

   public RegistryEntryLookup createMutableRegistryLookup() {
      this.assertNotFrozen();
      return new RegistryEntryLookup() {
         public Optional getOptional(RegistryKey key) {
            return Optional.of(this.getOrThrow(key));
         }

         public RegistryEntry.Reference getOrThrow(RegistryKey key) {
            return SimpleRegistry.this.getOrCreateEntry(key);
         }

         public Optional getOptional(TagKey tag) {
            return Optional.of(this.getOrThrow(tag));
         }

         public RegistryEntryList.Named getOrThrow(TagKey tag) {
            return SimpleRegistry.this.getTag(tag);
         }
      };
   }

   public Registry.PendingTagLoad startTagReload(TagGroupLoader.RegistryTags tags) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         ImmutableMap.Builder builder = ImmutableMap.builder();
         final Map map = new HashMap();
         tags.tags().forEach((key, values) -> {
            RegistryEntryList.Named named = (RegistryEntryList.Named)this.tags.get(key);
            if (named == null) {
               named = this.createNamedEntryList(key);
            }

            builder.put(key, named);
            map.put(key, List.copyOf(values));
         });
         final ImmutableMap immutableMap = builder.build();
         final RegistryWrapper.Impl impl = new RegistryWrapper.Impl.Delegating() {
            public RegistryWrapper.Impl getBase() {
               return SimpleRegistry.this;
            }

            public Optional getOptional(TagKey tag) {
               return Optional.ofNullable((RegistryEntryList.Named)immutableMap.get(tag));
            }

            public Stream getTags() {
               return immutableMap.values().stream();
            }
         };
         return new Registry.PendingTagLoad() {
            public RegistryKey getKey() {
               return SimpleRegistry.this.getKey();
            }

            public int size() {
               return map.size();
            }

            public RegistryWrapper.Impl getLookup() {
               return impl;
            }

            public void apply() {
               immutableMap.forEach((tagKey, named) -> {
                  List list = (List)map.getOrDefault(tagKey, List.of());
                  named.setEntries(list);
               });
               SimpleRegistry.this.tagLookup = SimpleRegistry.TagLookup.fromMap(immutableMap);
               SimpleRegistry.this.refreshTags();
            }
         };
      }
   }

   private interface TagLookup {
      static TagLookup ofUnbound() {
         return new TagLookup() {
            public boolean isBound() {
               return false;
            }

            public Optional getOptional(TagKey key) {
               throw new IllegalStateException("Tags not bound, trying to access " + String.valueOf(key));
            }

            public void forEach(BiConsumer consumer) {
               throw new IllegalStateException("Tags not bound");
            }

            public Stream stream() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static TagLookup fromMap(final Map map) {
         return new TagLookup() {
            public boolean isBound() {
               return true;
            }

            public Optional getOptional(TagKey key) {
               return Optional.ofNullable((RegistryEntryList.Named)map.get(key));
            }

            public void forEach(BiConsumer consumer) {
               map.forEach(consumer);
            }

            public Stream stream() {
               return map.values().stream();
            }
         };
      }

      boolean isBound();

      Optional getOptional(TagKey key);

      void forEach(BiConsumer consumer);

      Stream stream();
   }
}
