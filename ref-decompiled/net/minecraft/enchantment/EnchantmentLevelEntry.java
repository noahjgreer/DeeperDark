package net.minecraft.enchantment;

import net.minecraft.registry.entry.RegistryEntry;

public record EnchantmentLevelEntry(RegistryEntry enchantment, int level) {
   public EnchantmentLevelEntry(RegistryEntry enchantment, int level) {
      this.enchantment = enchantment;
      this.level = level;
   }

   public int getWeight() {
      return ((Enchantment)this.enchantment().value()).getWeight();
   }

   public RegistryEntry enchantment() {
      return this.enchantment;
   }

   public int level() {
      return this.level;
   }
}
