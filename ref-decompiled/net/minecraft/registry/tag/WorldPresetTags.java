package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class WorldPresetTags {
   public static final TagKey NORMAL = of("normal");
   public static final TagKey EXTENDED = of("extended");

   private WorldPresetTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.WORLD_PRESET, Identifier.ofVanilla(id));
   }
}
