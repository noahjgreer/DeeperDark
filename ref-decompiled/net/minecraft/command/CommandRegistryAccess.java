package net.minecraft.command;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

public interface CommandRegistryAccess extends RegistryWrapper.WrapperLookup {
   static CommandRegistryAccess of(final RegistryWrapper.WrapperLookup registries, final FeatureSet enabledFeatures) {
      return new CommandRegistryAccess() {
         public Stream streamAllRegistryKeys() {
            return registries.streamAllRegistryKeys();
         }

         public Optional getOptional(RegistryKey registryRef) {
            return registries.getOptional(registryRef).map((wrapper) -> {
               return wrapper.withFeatureFilter(enabledFeatures);
            });
         }

         public FeatureSet getEnabledFeatures() {
            return enabledFeatures;
         }
      };
   }

   FeatureSet getEnabledFeatures();
}
