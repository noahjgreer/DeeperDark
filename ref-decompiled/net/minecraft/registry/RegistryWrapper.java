package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public interface RegistryWrapper extends RegistryEntryLookup {
   Stream streamEntries();

   default Stream streamKeys() {
      return this.streamEntries().map(RegistryEntry.Reference::registryKey);
   }

   Stream getTags();

   default Stream streamTagKeys() {
      return this.getTags().map(RegistryEntryList.Named::getTag);
   }

   public interface WrapperLookup extends RegistryEntryLookup.RegistryLookup {
      Stream streamAllRegistryKeys();

      default Stream stream() {
         return this.streamAllRegistryKeys().map(this::getOrThrow);
      }

      Optional getOptional(RegistryKey registryRef);

      default Impl getOrThrow(RegistryKey registryRef) {
         return (Impl)this.getOptional(registryRef).orElseThrow(() -> {
            return new IllegalStateException("Registry " + String.valueOf(registryRef.getValue()) + " not found");
         });
      }

      default RegistryOps getOps(DynamicOps delegate) {
         return RegistryOps.of(delegate, this);
      }

      static WrapperLookup of(Stream wrappers) {
         final Map map = (Map)wrappers.collect(Collectors.toUnmodifiableMap(Impl::getKey, (wrapper) -> {
            return wrapper;
         }));
         return new WrapperLookup() {
            public Stream streamAllRegistryKeys() {
               return map.keySet().stream();
            }

            public Optional getOptional(RegistryKey registryRef) {
               return Optional.ofNullable((Impl)map.get(registryRef));
            }
         };
      }

      default Lifecycle getLifecycle() {
         return (Lifecycle)this.stream().map(Impl::getLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
      }

      // $FF: synthetic method
      default RegistryEntryLookup getOrThrow(final RegistryKey registryRef) {
         return this.getOrThrow(registryRef);
      }
   }

   public interface Impl extends RegistryWrapper, RegistryEntryOwner {
      RegistryKey getKey();

      Lifecycle getLifecycle();

      default Impl withFeatureFilter(FeatureSet enabledFeatures) {
         return ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getKey()) ? this.withPredicateFilter((feature) -> {
            return ((ToggleableFeature)feature).isEnabled(enabledFeatures);
         }) : this;
      }

      default Impl withPredicateFilter(final Predicate predicate) {
         return new Delegating() {
            public Impl getBase() {
               return Impl.this;
            }

            public Optional getOptional(RegistryKey key) {
               return this.getBase().getOptional(key).filter((entry) -> {
                  return predicate.test(entry.value());
               });
            }

            public Stream streamEntries() {
               return this.getBase().streamEntries().filter((entry) -> {
                  return predicate.test(entry.value());
               });
            }
         };
      }

      public interface Delegating extends Impl {
         Impl getBase();

         default RegistryKey getKey() {
            return this.getBase().getKey();
         }

         default Lifecycle getLifecycle() {
            return this.getBase().getLifecycle();
         }

         default Optional getOptional(RegistryKey key) {
            return this.getBase().getOptional(key);
         }

         default Stream streamEntries() {
            return this.getBase().streamEntries();
         }

         default Optional getOptional(TagKey tag) {
            return this.getBase().getOptional(tag);
         }

         default Stream getTags() {
            return this.getBase().getTags();
         }
      }
   }
}
