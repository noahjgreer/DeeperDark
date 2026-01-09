package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class RegistryBuilder {
   private final List registries = new ArrayList();

   static RegistryEntryLookup toLookup(final RegistryWrapper.Impl wrapper) {
      return new EntryListCreatingLookup(wrapper) {
         public Optional getOptional(RegistryKey key) {
            return wrapper.getOptional(key);
         }
      };
   }

   static RegistryWrapper.Impl createWrapper(final RegistryKey registryRef, final Lifecycle lifecycle, RegistryEntryOwner owner, final Map entries) {
      return new UntaggedLookup(owner) {
         public RegistryKey getKey() {
            return registryRef;
         }

         public Lifecycle getLifecycle() {
            return lifecycle;
         }

         public Optional getOptional(RegistryKey key) {
            return Optional.ofNullable((RegistryEntry.Reference)entries.get(key));
         }

         public Stream streamEntries() {
            return entries.values().stream();
         }
      };
   }

   public RegistryBuilder addRegistry(RegistryKey registryRef, Lifecycle lifecycle, BootstrapFunction bootstrapFunction) {
      this.registries.add(new RegistryInfo(registryRef, lifecycle, bootstrapFunction));
      return this;
   }

   public RegistryBuilder addRegistry(RegistryKey registryRef, BootstrapFunction bootstrapFunction) {
      return this.addRegistry(registryRef, Lifecycle.stable(), bootstrapFunction);
   }

   private Registries createBootstrappedRegistries(DynamicRegistryManager registryManager) {
      Registries registries = RegistryBuilder.Registries.of(registryManager, this.registries.stream().map(RegistryInfo::key));
      this.registries.forEach((registry) -> {
         registry.runBootstrap(registries);
      });
      return registries;
   }

   private static RegistryWrapper.WrapperLookup createWrapperLookup(AnyOwner entryOwner, DynamicRegistryManager registryManager, Stream wrappers) {
      final Map map = new HashMap();
      registryManager.streamAllRegistries().forEach((registry) -> {
         map.put(registry.key(), WrapperInfoPair.of(registry.value()));
      });
      wrappers.forEach((wrapper) -> {
         map.put(wrapper.getKey(), WrapperInfoPair.of(entryOwner, wrapper));
      });
      return new RegistryWrapper.WrapperLookup() {
         public Stream streamAllRegistryKeys() {
            return map.keySet().stream();
         }

         Optional get(RegistryKey registryRef) {
            return Optional.ofNullable((WrapperInfoPair)map.get(registryRef));
         }

         public Optional getOptional(RegistryKey registryRef) {
            return this.get(registryRef).map(WrapperInfoPair::lookup);
         }

         public RegistryOps getOps(DynamicOps delegate) {
            return RegistryOps.of(delegate, new RegistryOps.RegistryInfoGetter() {
               public Optional getRegistryInfo(RegistryKey registryRef) {
                  return get(registryRef).map(WrapperInfoPair::opsInfo);
               }
            });
         }
      };

      record WrapperInfoPair(RegistryWrapper.Impl lookup, RegistryOps.RegistryInfo opsInfo) {
         WrapperInfoPair(RegistryWrapper.Impl impl, RegistryOps.RegistryInfo registryInfo) {
            this.lookup = impl;
            this.opsInfo = registryInfo;
         }

         public static WrapperInfoPair of(RegistryWrapper.Impl wrapper) {
            return new WrapperInfoPair(new UntaggedDelegatingLookup(wrapper, wrapper), RegistryOps.RegistryInfo.fromWrapper(wrapper));
         }

         public static WrapperInfoPair of(AnyOwner owner, RegistryWrapper.Impl wrapper) {
            return new WrapperInfoPair(new UntaggedDelegatingLookup(owner.downcast(), wrapper), new RegistryOps.RegistryInfo(owner.downcast(), wrapper, wrapper.getLifecycle()));
         }

         public RegistryWrapper.Impl lookup() {
            return this.lookup;
         }

         public RegistryOps.RegistryInfo opsInfo() {
            return this.opsInfo;
         }
      }

   }

   public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager registryManager) {
      Registries registries = this.createBootstrappedRegistries(registryManager);
      Stream stream = this.registries.stream().map((info) -> {
         return info.init(registries).toWrapper(registries.owner);
      });
      RegistryWrapper.WrapperLookup wrapperLookup = createWrapperLookup(registries.owner, registryManager, stream);
      registries.checkUnreferencedKeys();
      registries.checkOrphanedValues();
      registries.throwErrors();
      return wrapperLookup;
   }

   private RegistryWrapper.WrapperLookup createFullWrapperLookup(DynamicRegistryManager registryManager, RegistryWrapper.WrapperLookup base, RegistryCloner.CloneableRegistries cloneableRegistries, Map initializedRegistries, RegistryWrapper.WrapperLookup patches) {
      AnyOwner anyOwner = new AnyOwner();
      MutableObject mutableObject = new MutableObject();
      List list = (List)initializedRegistries.keySet().stream().map((registryRef) -> {
         return this.applyPatches(anyOwner, cloneableRegistries, registryRef, patches, base, mutableObject);
      }).collect(Collectors.toUnmodifiableList());
      RegistryWrapper.WrapperLookup wrapperLookup = createWrapperLookup(anyOwner, registryManager, list.stream());
      mutableObject.setValue(wrapperLookup);
      return wrapperLookup;
   }

   private RegistryWrapper.Impl applyPatches(RegistryEntryOwner owner, RegistryCloner.CloneableRegistries cloneableRegistries, RegistryKey registryRef, RegistryWrapper.WrapperLookup patches, RegistryWrapper.WrapperLookup base, MutableObject lazyWrapper) {
      RegistryCloner registryCloner = cloneableRegistries.get(registryRef);
      if (registryCloner == null) {
         throw new NullPointerException("No cloner for " + String.valueOf(registryRef.getValue()));
      } else {
         Map map = new HashMap();
         RegistryWrapper.Impl impl = patches.getOrThrow(registryRef);
         impl.streamEntries().forEach((entry) -> {
            RegistryKey registryKey = entry.registryKey();
            LazyReferenceEntry lazyReferenceEntry = new LazyReferenceEntry(owner, registryKey);
            lazyReferenceEntry.supplier = () -> {
               return registryCloner.clone(entry.value(), patches, (RegistryWrapper.WrapperLookup)lazyWrapper.getValue());
            };
            map.put(registryKey, lazyReferenceEntry);
         });
         RegistryWrapper.Impl impl2 = base.getOrThrow(registryRef);
         impl2.streamEntries().forEach((entry) -> {
            RegistryKey registryKey = entry.registryKey();
            map.computeIfAbsent(registryKey, (key) -> {
               LazyReferenceEntry lazyReferenceEntry = new LazyReferenceEntry(owner, registryKey);
               lazyReferenceEntry.supplier = () -> {
                  return registryCloner.clone(entry.value(), base, (RegistryWrapper.WrapperLookup)lazyWrapper.getValue());
               };
               return lazyReferenceEntry;
            });
         });
         Lifecycle lifecycle = impl.getLifecycle().add(impl2.getLifecycle());
         return createWrapper(registryRef, lifecycle, owner, map);
      }
   }

   public FullPatchesRegistriesPair createWrapperLookup(DynamicRegistryManager baseRegistryManager, RegistryWrapper.WrapperLookup registries, RegistryCloner.CloneableRegistries cloneableRegistries) {
      Registries registries2 = this.createBootstrappedRegistries(baseRegistryManager);
      Map map = new HashMap();
      this.registries.stream().map((info) -> {
         return info.init(registries2);
      }).forEach((registry) -> {
         map.put(registry.key, registry);
      });
      Set set = (Set)baseRegistryManager.streamAllRegistryKeys().collect(Collectors.toUnmodifiableSet());
      registries.streamAllRegistryKeys().filter((key) -> {
         return !set.contains(key);
      }).forEach((key) -> {
         map.putIfAbsent(key, new InitializedRegistry(key, Lifecycle.stable(), Map.of()));
      });
      Stream stream = map.values().stream().map((registry) -> {
         return registry.toWrapper(registries2.owner);
      });
      RegistryWrapper.WrapperLookup wrapperLookup = createWrapperLookup(registries2.owner, baseRegistryManager, stream);
      registries2.checkOrphanedValues();
      registries2.throwErrors();
      RegistryWrapper.WrapperLookup wrapperLookup2 = this.createFullWrapperLookup(baseRegistryManager, registries, cloneableRegistries, map, wrapperLookup);
      return new FullPatchesRegistriesPair(wrapperLookup2, wrapperLookup);
   }

   static record RegistryInfo(RegistryKey key, Lifecycle lifecycle, BootstrapFunction bootstrap) {
      RegistryInfo(RegistryKey registryKey, Lifecycle lifecycle, BootstrapFunction bootstrapFunction) {
         this.key = registryKey;
         this.lifecycle = lifecycle;
         this.bootstrap = bootstrapFunction;
      }

      void runBootstrap(Registries registries) {
         this.bootstrap.run(registries.createRegisterable());
      }

      public InitializedRegistry init(Registries registries) {
         Map map = new HashMap();
         Iterator iterator = registries.registeredValues.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            RegistryKey registryKey = (RegistryKey)entry.getKey();
            if (registryKey.isOf(this.key)) {
               RegisteredValue registeredValue = (RegisteredValue)entry.getValue();
               RegistryEntry.Reference reference = (RegistryEntry.Reference)registries.lookup.keysToEntries.remove(registryKey);
               map.put(registryKey, new EntryAssociatedValue(registeredValue, Optional.ofNullable(reference)));
               iterator.remove();
            }
         }

         return new InitializedRegistry(this.key, this.lifecycle, map);
      }

      public RegistryKey key() {
         return this.key;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }

      public BootstrapFunction bootstrap() {
         return this.bootstrap;
      }
   }

   @FunctionalInterface
   public interface BootstrapFunction {
      void run(Registerable registerable);
   }

   static record Registries(AnyOwner owner, StandAloneEntryCreatingLookup lookup, Map registries, Map registeredValues, List errors) {
      final AnyOwner owner;
      final StandAloneEntryCreatingLookup lookup;
      final Map registries;
      final Map registeredValues;
      final List errors;

      private Registries(AnyOwner anyOwner, StandAloneEntryCreatingLookup standAloneEntryCreatingLookup, Map map, Map map2, List list) {
         this.owner = anyOwner;
         this.lookup = standAloneEntryCreatingLookup;
         this.registries = map;
         this.registeredValues = map2;
         this.errors = list;
      }

      public static Registries of(DynamicRegistryManager dynamicRegistryManager, Stream registryRefs) {
         AnyOwner anyOwner = new AnyOwner();
         List list = new ArrayList();
         StandAloneEntryCreatingLookup standAloneEntryCreatingLookup = new StandAloneEntryCreatingLookup(anyOwner);
         ImmutableMap.Builder builder = ImmutableMap.builder();
         dynamicRegistryManager.streamAllRegistries().forEach((entry) -> {
            builder.put(entry.key().getValue(), RegistryBuilder.toLookup(entry.value()));
         });
         registryRefs.forEach((registryRef) -> {
            builder.put(registryRef.getValue(), standAloneEntryCreatingLookup);
         });
         return new Registries(anyOwner, standAloneEntryCreatingLookup, builder.build(), new HashMap(), list);
      }

      public Registerable createRegisterable() {
         return new Registerable() {
            public RegistryEntry.Reference register(RegistryKey key, Object value, Lifecycle lifecycle) {
               RegisteredValue registeredValue = (RegisteredValue)Registries.this.registeredValues.put(key, new RegisteredValue(value, lifecycle));
               if (registeredValue != null) {
                  List var10000 = Registries.this.errors;
                  String var10003 = String.valueOf(key);
                  var10000.add(new IllegalStateException("Duplicate registration for " + var10003 + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(registeredValue.value)));
               }

               return Registries.this.lookup.getOrCreate(key);
            }

            public RegistryEntryLookup getRegistryLookup(RegistryKey registryRef) {
               return (RegistryEntryLookup)Registries.this.registries.getOrDefault(registryRef.getValue(), Registries.this.lookup);
            }
         };
      }

      public void checkOrphanedValues() {
         this.registeredValues.forEach((key, value) -> {
            List var10000 = this.errors;
            String var10003 = String.valueOf(value.value);
            var10000.add(new IllegalStateException("Orpaned value " + var10003 + " for key " + String.valueOf(key)));
         });
      }

      public void checkUnreferencedKeys() {
         Iterator var1 = this.lookup.keysToEntries.keySet().iterator();

         while(var1.hasNext()) {
            RegistryKey registryKey = (RegistryKey)var1.next();
            this.errors.add(new IllegalStateException("Unreferenced key: " + String.valueOf(registryKey)));
         }

      }

      public void throwErrors() {
         if (!this.errors.isEmpty()) {
            IllegalStateException illegalStateException = new IllegalStateException("Errors during registry creation");
            Iterator var2 = this.errors.iterator();

            while(var2.hasNext()) {
               RuntimeException runtimeException = (RuntimeException)var2.next();
               illegalStateException.addSuppressed(runtimeException);
            }

            throw illegalStateException;
         }
      }

      public AnyOwner owner() {
         return this.owner;
      }

      public StandAloneEntryCreatingLookup lookup() {
         return this.lookup;
      }

      public Map registries() {
         return this.registries;
      }

      public Map registeredValues() {
         return this.registeredValues;
      }

      public List errors() {
         return this.errors;
      }
   }

   static class AnyOwner implements RegistryEntryOwner {
      public RegistryEntryOwner downcast() {
         return this;
      }
   }

   public static record FullPatchesRegistriesPair(RegistryWrapper.WrapperLookup full, RegistryWrapper.WrapperLookup patches) {
      public FullPatchesRegistriesPair(RegistryWrapper.WrapperLookup wrapperLookup, RegistryWrapper.WrapperLookup wrapperLookup2) {
         this.full = wrapperLookup;
         this.patches = wrapperLookup2;
      }

      public RegistryWrapper.WrapperLookup full() {
         return this.full;
      }

      public RegistryWrapper.WrapperLookup patches() {
         return this.patches;
      }
   }

   private static record InitializedRegistry(RegistryKey key, Lifecycle lifecycle, Map values) {
      final RegistryKey key;

      InitializedRegistry(RegistryKey registryKey, Lifecycle lifecycle, Map map) {
         this.key = registryKey;
         this.lifecycle = lifecycle;
         this.values = map;
      }

      public RegistryWrapper.Impl toWrapper(AnyOwner anyOwner) {
         Map map = (Map)this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (entry) -> {
            EntryAssociatedValue entryAssociatedValue = (EntryAssociatedValue)entry.getValue();
            RegistryEntry.Reference reference = (RegistryEntry.Reference)entryAssociatedValue.entry().orElseGet(() -> {
               return RegistryEntry.Reference.standAlone(anyOwner.downcast(), (RegistryKey)entry.getKey());
            });
            reference.setValue(entryAssociatedValue.value().value());
            return reference;
         }));
         return RegistryBuilder.createWrapper(this.key, this.lifecycle, anyOwner.downcast(), map);
      }

      public RegistryKey key() {
         return this.key;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }

      public Map values() {
         return this.values;
      }
   }

   static class LazyReferenceEntry extends RegistryEntry.Reference {
      @Nullable
      Supplier supplier;

      protected LazyReferenceEntry(RegistryEntryOwner owner, @Nullable RegistryKey key) {
         super(RegistryEntry.Reference.Type.STAND_ALONE, owner, key, (Object)null);
      }

      protected void setValue(Object value) {
         super.setValue(value);
         this.supplier = null;
      }

      public Object value() {
         if (this.supplier != null) {
            this.setValue(this.supplier.get());
         }

         return super.value();
      }
   }

   private static record EntryAssociatedValue(RegisteredValue value, Optional entry) {
      EntryAssociatedValue(RegisteredValue registeredValue, Optional optional) {
         this.value = registeredValue;
         this.entry = optional;
      }

      public RegisteredValue value() {
         return this.value;
      }

      public Optional entry() {
         return this.entry;
      }
   }

   static record RegisteredValue(Object value, Lifecycle lifecycle) {
      final Object value;

      RegisteredValue(Object object, Lifecycle lifecycle) {
         this.value = object;
         this.lifecycle = lifecycle;
      }

      public Object value() {
         return this.value;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }
   }

   private static class StandAloneEntryCreatingLookup extends EntryListCreatingLookup {
      final Map keysToEntries = new HashMap();

      public StandAloneEntryCreatingLookup(RegistryEntryOwner registryEntryOwner) {
         super(registryEntryOwner);
      }

      public Optional getOptional(RegistryKey key) {
         return Optional.of(this.getOrCreate(key));
      }

      RegistryEntry.Reference getOrCreate(RegistryKey key) {
         return (RegistryEntry.Reference)this.keysToEntries.computeIfAbsent(key, (key2) -> {
            return RegistryEntry.Reference.standAlone(this.entryOwner, key2);
         });
      }
   }

   static class UntaggedDelegatingLookup extends UntaggedLookup implements RegistryWrapper.Impl.Delegating {
      private final RegistryWrapper.Impl base;

      UntaggedDelegatingLookup(RegistryEntryOwner entryOwner, RegistryWrapper.Impl base) {
         super(entryOwner);
         this.base = base;
      }

      public RegistryWrapper.Impl getBase() {
         return this.base;
      }
   }

   private abstract static class UntaggedLookup extends EntryListCreatingLookup implements RegistryWrapper.Impl {
      protected UntaggedLookup(RegistryEntryOwner registryEntryOwner) {
         super(registryEntryOwner);
      }

      public Stream getTags() {
         throw new UnsupportedOperationException("Tags are not available in datagen");
      }
   }

   private abstract static class EntryListCreatingLookup implements RegistryEntryLookup {
      protected final RegistryEntryOwner entryOwner;

      protected EntryListCreatingLookup(RegistryEntryOwner entryOwner) {
         this.entryOwner = entryOwner;
      }

      public Optional getOptional(TagKey tag) {
         return Optional.of(RegistryEntryList.of(this.entryOwner, tag));
      }
   }
}
