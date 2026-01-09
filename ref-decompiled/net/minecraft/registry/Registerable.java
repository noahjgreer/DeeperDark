package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.entry.RegistryEntry;

public interface Registerable {
   RegistryEntry.Reference register(RegistryKey key, Object value, Lifecycle lifecycle);

   default RegistryEntry.Reference register(RegistryKey key, Object value) {
      return this.register(key, value, Lifecycle.stable());
   }

   RegistryEntryLookup getRegistryLookup(RegistryKey registryRef);
}
