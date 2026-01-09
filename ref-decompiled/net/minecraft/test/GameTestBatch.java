package net.minecraft.test;

import java.util.Collection;
import net.minecraft.registry.entry.RegistryEntry;

public record GameTestBatch(int index, Collection states, RegistryEntry environment) {
   public GameTestBatch(int i, Collection testFunctions, RegistryEntry registryEntry) {
      if (testFunctions.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
      } else {
         this.index = i;
         this.states = testFunctions;
         this.environment = registryEntry;
      }
   }

   public int index() {
      return this.index;
   }

   public Collection states() {
      return this.states;
   }

   public RegistryEntry environment() {
      return this.environment;
   }
}
