package net.minecraft.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

public class RegistryOps extends ForwardingDynamicOps {
   private final RegistryInfoGetter registryInfoGetter;

   public static RegistryOps of(DynamicOps delegate, RegistryWrapper.WrapperLookup registries) {
      return of(delegate, (RegistryInfoGetter)(new CachedRegistryInfoGetter(registries)));
   }

   public static RegistryOps of(DynamicOps delegate, RegistryInfoGetter registryInfoGetter) {
      return new RegistryOps(delegate, registryInfoGetter);
   }

   public static Dynamic withRegistry(Dynamic dynamic, RegistryWrapper.WrapperLookup registries) {
      return new Dynamic(registries.getOps(dynamic.getOps()), dynamic.getValue());
   }

   private RegistryOps(DynamicOps delegate, RegistryInfoGetter registryInfoGetter) {
      super(delegate);
      this.registryInfoGetter = registryInfoGetter;
   }

   public RegistryOps withDelegate(DynamicOps delegate) {
      return delegate == this.delegate ? this : new RegistryOps(delegate, this.registryInfoGetter);
   }

   public Optional getOwner(RegistryKey registryRef) {
      return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryInfo::owner);
   }

   public Optional getEntryLookup(RegistryKey registryRef) {
      return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryInfo::entryLookup);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RegistryOps registryOps = (RegistryOps)o;
         return this.delegate.equals(registryOps.delegate) && this.registryInfoGetter.equals(registryOps.registryInfoGetter);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.registryInfoGetter.hashCode();
   }

   public static RecordCodecBuilder getEntryLookupCodec(RegistryKey registryRef) {
      return Codecs.createContextRetrievalCodec((ops) -> {
         if (ops instanceof RegistryOps registryOps) {
            return (DataResult)registryOps.registryInfoGetter.getRegistryInfo(registryRef).map((info) -> {
               return DataResult.success(info.entryLookup(), info.elementsLifecycle());
            }).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "Unknown registry: " + String.valueOf(registryRef);
               });
            });
         } else {
            return DataResult.error(() -> {
               return "Not a registry ops";
            });
         }
      }).forGetter((object) -> {
         return null;
      });
   }

   public static RecordCodecBuilder getEntryCodec(RegistryKey key) {
      RegistryKey registryKey = RegistryKey.ofRegistry(key.getRegistry());
      return Codecs.createContextRetrievalCodec((ops) -> {
         if (ops instanceof RegistryOps registryOps) {
            return (DataResult)registryOps.registryInfoGetter.getRegistryInfo(registryKey).flatMap((info) -> {
               return info.entryLookup().getOptional(key);
            }).map(DataResult::success).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "Can't find value: " + String.valueOf(key);
               });
            });
         } else {
            return DataResult.error(() -> {
               return "Not a registry ops";
            });
         }
      }).forGetter((object) -> {
         return null;
      });
   }

   private static final class CachedRegistryInfoGetter implements RegistryInfoGetter {
      private final RegistryWrapper.WrapperLookup registries;
      private final Map cache = new ConcurrentHashMap();

      public CachedRegistryInfoGetter(RegistryWrapper.WrapperLookup registries) {
         this.registries = registries;
      }

      public Optional getRegistryInfo(RegistryKey registryRef) {
         return (Optional)this.cache.computeIfAbsent(registryRef, this::compute);
      }

      private Optional compute(RegistryKey registryRef) {
         return this.registries.getOptional(registryRef).map(RegistryInfo::fromWrapper);
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            boolean var10000;
            if (o instanceof CachedRegistryInfoGetter) {
               CachedRegistryInfoGetter cachedRegistryInfoGetter = (CachedRegistryInfoGetter)o;
               if (this.registries.equals(cachedRegistryInfoGetter.registries)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      public int hashCode() {
         return this.registries.hashCode();
      }
   }

   public interface RegistryInfoGetter {
      Optional getRegistryInfo(RegistryKey registryRef);
   }

   public static record RegistryInfo(RegistryEntryOwner owner, RegistryEntryLookup entryLookup, Lifecycle elementsLifecycle) {
      public RegistryInfo(RegistryEntryOwner registryEntryOwner, RegistryEntryLookup registryEntryLookup, Lifecycle lifecycle) {
         this.owner = registryEntryOwner;
         this.entryLookup = registryEntryLookup;
         this.elementsLifecycle = lifecycle;
      }

      public static RegistryInfo fromWrapper(RegistryWrapper.Impl wrapper) {
         return new RegistryInfo(wrapper, wrapper, wrapper.getLifecycle());
      }

      public RegistryEntryOwner owner() {
         return this.owner;
      }

      public RegistryEntryLookup entryLookup() {
         return this.entryLookup;
      }

      public Lifecycle elementsLifecycle() {
         return this.elementsLifecycle;
      }
   }
}
