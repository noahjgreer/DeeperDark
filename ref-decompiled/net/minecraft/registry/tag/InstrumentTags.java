package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface InstrumentTags {
   TagKey REGULAR_GOAT_HORNS = of("regular_goat_horns");
   TagKey SCREAMING_GOAT_HORNS = of("screaming_goat_horns");
   TagKey GOAT_HORNS = of("goat_horns");

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.INSTRUMENT, Identifier.ofVanilla(id));
   }
}
