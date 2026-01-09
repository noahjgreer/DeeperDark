package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public interface EnchantmentProviderType {
   static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"by_cost", ByCostEnchantmentProvider.CODEC);
      Registry.register(registry, (String)"by_cost_with_difficulty", ByCostWithDifficultyEnchantmentProvider.CODEC);
      return (MapCodec)Registry.register(registry, (String)"single", SingleEnchantmentProvider.CODEC);
   }
}
