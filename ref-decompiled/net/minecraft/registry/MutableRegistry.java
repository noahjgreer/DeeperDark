package net.minecraft.registry;

import java.util.List;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagKey;

public interface MutableRegistry extends Registry {
   RegistryEntry.Reference add(RegistryKey key, Object value, RegistryEntryInfo info);

   void setEntries(TagKey tag, List entries);

   boolean isEmpty();

   RegistryEntryLookup createMutableRegistryLookup();
}
