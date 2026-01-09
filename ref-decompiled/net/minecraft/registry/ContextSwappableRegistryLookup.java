package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;

public class ContextSwappableRegistryLookup implements RegistryEntryLookup.RegistryLookup {
   final RegistryWrapper.WrapperLookup delegate;
   final EntryLookupImpl entryLookupImpl = new EntryLookupImpl();
   final Map entries = new HashMap();
   final Map tags = new HashMap();

   public ContextSwappableRegistryLookup(RegistryWrapper.WrapperLookup delegate) {
      this.delegate = delegate;
   }

   public Optional getOptional(RegistryKey registryRef) {
      return Optional.of(this.entryLookupImpl.asEntryLookup());
   }

   public RegistryOps createRegistryOps(DynamicOps delegateOps) {
      return RegistryOps.of(delegateOps, new RegistryOps.RegistryInfoGetter() {
         public Optional getRegistryInfo(RegistryKey registryRef) {
            return ContextSwappableRegistryLookup.this.delegate.getOptional(registryRef).map(RegistryOps.RegistryInfo::fromWrapper).or(() -> {
               return Optional.of(new RegistryOps.RegistryInfo(ContextSwappableRegistryLookup.this.entryLookupImpl.asEntryOwner(), ContextSwappableRegistryLookup.this.entryLookupImpl.asEntryLookup(), Lifecycle.experimental()));
            });
         }
      });
   }

   public ContextSwapper createContextSwapper() {
      return new ContextSwapper() {
         public DataResult swapContext(Codec codec, Object value, RegistryWrapper.WrapperLookup registries) {
            return codec.encodeStart(ContextSwappableRegistryLookup.this.createRegistryOps(JavaOps.INSTANCE), value).flatMap((encodedValue) -> {
               return codec.parse(registries.getOps(JavaOps.INSTANCE), encodedValue);
            });
         }
      };
   }

   public boolean hasEntries() {
      return !this.entries.isEmpty() || !this.tags.isEmpty();
   }

   class EntryLookupImpl implements RegistryEntryLookup, RegistryEntryOwner {
      public Optional getOptional(RegistryKey key) {
         return Optional.of(this.getOrComputeEntry(key));
      }

      public RegistryEntry.Reference getOrThrow(RegistryKey key) {
         return this.getOrComputeEntry(key);
      }

      private RegistryEntry.Reference getOrComputeEntry(RegistryKey key) {
         return (RegistryEntry.Reference)ContextSwappableRegistryLookup.this.entries.computeIfAbsent(key, (key2) -> {
            return RegistryEntry.Reference.standAlone(this, key2);
         });
      }

      public Optional getOptional(TagKey tag) {
         return Optional.of(this.getOrComputeTag(tag));
      }

      public RegistryEntryList.Named getOrThrow(TagKey tag) {
         return this.getOrComputeTag(tag);
      }

      private RegistryEntryList.Named getOrComputeTag(TagKey tag) {
         return (RegistryEntryList.Named)ContextSwappableRegistryLookup.this.tags.computeIfAbsent(tag, (tagKey) -> {
            return RegistryEntryList.of((RegistryEntryOwner)this, (TagKey)tagKey);
         });
      }

      public RegistryEntryLookup asEntryLookup() {
         return this;
      }

      public RegistryEntryOwner asEntryOwner() {
         return this;
      }
   }
}
