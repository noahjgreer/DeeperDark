package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class GameEventTags {
   public static final TagKey VIBRATIONS = of("vibrations");
   public static final TagKey WARDEN_CAN_LISTEN = of("warden_can_listen");
   public static final TagKey SHRIEKER_CAN_LISTEN = of("shrieker_can_listen");
   public static final TagKey IGNORE_VIBRATIONS_SNEAKING = of("ignore_vibrations_sneaking");
   public static final TagKey ALLAY_CAN_LISTEN = of("allay_can_listen");

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.GAME_EVENT, Identifier.ofVanilla(id));
   }
}
