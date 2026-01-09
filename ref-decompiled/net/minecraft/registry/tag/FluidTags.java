package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class FluidTags {
   public static final TagKey WATER = of("water");
   public static final TagKey LAVA = of("lava");

   private FluidTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.FLUID, Identifier.ofVanilla(id));
   }
}
