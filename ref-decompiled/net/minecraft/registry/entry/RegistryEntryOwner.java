package net.minecraft.registry.entry;

public interface RegistryEntryOwner {
   default boolean ownerEquals(RegistryEntryOwner other) {
      return other == this;
   }
}
