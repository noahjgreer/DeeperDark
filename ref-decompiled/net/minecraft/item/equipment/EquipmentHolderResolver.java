package net.minecraft.item.equipment;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;

@FunctionalInterface
public interface EquipmentHolderResolver {
   RegistryEntryList get(RegistryEntryLookup registry);
}
