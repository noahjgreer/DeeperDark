package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class FlatLevelGeneratorPresetTags {
   public static final TagKey VISIBLE = of("visible");

   private FlatLevelGeneratorPresetTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, Identifier.ofVanilla(id));
   }
}
