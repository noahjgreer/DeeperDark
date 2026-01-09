package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PaintingVariantTags {
   public static final TagKey PLACEABLE = of("placeable");

   private PaintingVariantTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.PAINTING_VARIANT, Identifier.ofVanilla(id));
   }
}
