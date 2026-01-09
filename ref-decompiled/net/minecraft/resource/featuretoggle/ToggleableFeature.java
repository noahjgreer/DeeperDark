package net.minecraft.resource.featuretoggle;

import java.util.Set;
import net.minecraft.registry.RegistryKeys;

public interface ToggleableFeature {
   Set FEATURE_ENABLED_REGISTRY_KEYS = Set.of(RegistryKeys.ITEM, RegistryKeys.BLOCK, RegistryKeys.ENTITY_TYPE, RegistryKeys.SCREEN_HANDLER, RegistryKeys.POTION, RegistryKeys.STATUS_EFFECT);

   FeatureSet getRequiredFeatures();

   default boolean isEnabled(FeatureSet enabledFeatures) {
      return this.getRequiredFeatures().isSubsetOf(enabledFeatures);
   }
}
