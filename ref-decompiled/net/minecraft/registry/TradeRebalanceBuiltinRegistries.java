package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.enchantment.provider.TradeRebalanceEnchantmentProviders;

public class TradeRebalanceBuiltinRegistries {
   private static final RegistryBuilder REGISTRY_BUILDER;

   public static CompletableFuture validate(CompletableFuture registriesFuture) {
      return ExperimentalRegistriesValidator.validate(registriesFuture, REGISTRY_BUILDER);
   }

   static {
      REGISTRY_BUILDER = (new RegistryBuilder()).addRegistry(RegistryKeys.ENCHANTMENT_PROVIDER, TradeRebalanceEnchantmentProviders::bootstrap);
   }
}
