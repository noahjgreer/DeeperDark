package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;

public interface DynamicRegistryManager extends RegistryWrapper.WrapperLookup {
   Logger LOGGER = LogUtils.getLogger();
   Immutable EMPTY = (new ImmutableImpl(Map.of())).toImmutable();

   Optional getOptional(RegistryKey registryRef);

   default Registry getOrThrow(RegistryKey key) {
      return (Registry)this.getOptional(key).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + String.valueOf(key));
      });
   }

   Stream streamAllRegistries();

   default Stream streamAllRegistryKeys() {
      return this.streamAllRegistries().map((registry) -> {
         return registry.key;
      });
   }

   static Immutable of(final Registry registries) {
      return new Immutable() {
         public Optional getOptional(RegistryKey registryRef) {
            Registry registry = registries;
            return registry.getOptionalValue(registryRef);
         }

         public Stream streamAllRegistries() {
            return registries.getEntrySet().stream().map(Entry::of);
         }

         public Immutable toImmutable() {
            return this;
         }
      };
   }

   default Immutable toImmutable() {
      class Immutablized extends ImmutableImpl implements Immutable {
         protected Immutablized(final DynamicRegistryManager dynamicRegistryManager, final Stream entryStream) {
            super(entryStream);
         }
      }

      return new Immutablized(this, this.streamAllRegistries().map(Entry::freeze));
   }

   // $FF: synthetic method
   default RegistryWrapper.Impl getOrThrow(final RegistryKey registryRef) {
      return this.getOrThrow(registryRef);
   }

   // $FF: synthetic method
   default RegistryEntryLookup getOrThrow(final RegistryKey registryRef) {
      return this.getOrThrow(registryRef);
   }

   public static record Entry(RegistryKey key, Registry value) {
      final RegistryKey key;

      public Entry(RegistryKey registryKey, Registry registry) {
         this.key = registryKey;
         this.value = registry;
      }

      private static Entry of(Map.Entry entry) {
         return of((RegistryKey)entry.getKey(), (Registry)entry.getValue());
      }

      private static Entry of(RegistryKey key, Registry value) {
         return new Entry(key, value);
      }

      private Entry freeze() {
         return new Entry(this.key, this.value.freeze());
      }

      public RegistryKey key() {
         return this.key;
      }

      public Registry value() {
         return this.value;
      }
   }

   public static class ImmutableImpl implements DynamicRegistryManager {
      private final Map registries;

      public ImmutableImpl(List registries) {
         this.registries = (Map)registries.stream().collect(Collectors.toUnmodifiableMap(Registry::getKey, (registry) -> {
            return registry;
         }));
      }

      public ImmutableImpl(Map registries) {
         this.registries = Map.copyOf(registries);
      }

      public ImmutableImpl(Stream entryStream) {
         this.registries = (Map)entryStream.collect(ImmutableMap.toImmutableMap(Entry::key, Entry::value));
      }

      public Optional getOptional(RegistryKey registryRef) {
         return Optional.ofNullable((Registry)this.registries.get(registryRef)).map((registry) -> {
            return registry;
         });
      }

      public Stream streamAllRegistries() {
         return this.registries.entrySet().stream().map(Entry::of);
      }
   }

   public interface Immutable extends DynamicRegistryManager {
   }
}
