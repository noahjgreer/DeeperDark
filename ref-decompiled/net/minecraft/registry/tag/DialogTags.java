package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DialogTags {
   public static final TagKey PAUSE_SCREEN_ADDITIONS = of("pause_screen_additions");
   public static final TagKey QUICK_ACTIONS = of("quick_actions");

   private DialogTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.DIALOG, Identifier.ofVanilla(id));
   }
}
