package net.minecraft.world.storage;

import net.minecraft.registry.RegistryKey;

public record StorageKey(String level, RegistryKey dimension, String type) {
   public StorageKey(String string, RegistryKey registryKey, String string2) {
      this.level = string;
      this.dimension = registryKey;
      this.type = string2;
   }

   public StorageKey withSuffix(String suffix) {
      return new StorageKey(this.level, this.dimension, this.type + suffix);
   }

   public String level() {
      return this.level;
   }

   public RegistryKey dimension() {
      return this.dimension;
   }

   public String type() {
      return this.type;
   }
}
