package net.minecraft.registry;

import com.mojang.datafixers.DataFixUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExperimentalRegistriesValidator {
   public static CompletableFuture validate(CompletableFuture registriesFuture, RegistryBuilder builder) {
      return registriesFuture.thenApply((registries) -> {
         DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
         RegistryCloner.CloneableRegistries cloneableRegistries = new RegistryCloner.CloneableRegistries();
         RegistryLoader.DYNAMIC_REGISTRIES.forEach((entry) -> {
            Objects.requireNonNull(cloneableRegistries);
            entry.addToCloner(cloneableRegistries::add);
         });
         RegistryBuilder.FullPatchesRegistriesPair fullPatchesRegistriesPair = builder.createWrapperLookup((DynamicRegistryManager)immutable, (RegistryWrapper.WrapperLookup)registries, (RegistryCloner.CloneableRegistries)cloneableRegistries);
         RegistryWrapper.WrapperLookup wrapperLookup = fullPatchesRegistriesPair.full();
         Optional optional = wrapperLookup.getOptional(RegistryKeys.BIOME);
         Optional optional2 = wrapperLookup.getOptional(RegistryKeys.PLACED_FEATURE);
         if (optional.isPresent() || optional2.isPresent()) {
            BuiltinRegistries.validate((RegistryEntryLookup)DataFixUtils.orElseGet(optional2, () -> {
               return registries.getOrThrow(RegistryKeys.PLACED_FEATURE);
            }), (RegistryWrapper)DataFixUtils.orElseGet(optional, () -> {
               return registries.getOrThrow(RegistryKeys.BIOME);
            }));
         }

         return fullPatchesRegistriesPair;
      });
   }
}
