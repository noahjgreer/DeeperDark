package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypeTags {
   TagKey DAMAGES_HELMET = of("damages_helmet");
   TagKey BYPASSES_ARMOR = of("bypasses_armor");
   TagKey BYPASSES_SHIELD = of("bypasses_shield");
   TagKey BYPASSES_INVULNERABILITY = of("bypasses_invulnerability");
   TagKey BYPASSES_COOLDOWN = of("bypasses_cooldown");
   TagKey BYPASSES_EFFECTS = of("bypasses_effects");
   TagKey BYPASSES_RESISTANCE = of("bypasses_resistance");
   TagKey BYPASSES_ENCHANTMENTS = of("bypasses_enchantments");
   TagKey IS_FIRE = of("is_fire");
   TagKey IS_PROJECTILE = of("is_projectile");
   TagKey WITCH_RESISTANT_TO = of("witch_resistant_to");
   TagKey IS_EXPLOSION = of("is_explosion");
   TagKey IS_FALL = of("is_fall");
   TagKey IS_DROWNING = of("is_drowning");
   TagKey IS_FREEZING = of("is_freezing");
   TagKey IS_LIGHTNING = of("is_lightning");
   TagKey NO_ANGER = of("no_anger");
   TagKey NO_IMPACT = of("no_impact");
   TagKey ALWAYS_MOST_SIGNIFICANT_FALL = of("always_most_significant_fall");
   TagKey WITHER_IMMUNE_TO = of("wither_immune_to");
   TagKey IGNITES_ARMOR_STANDS = of("ignites_armor_stands");
   TagKey BURNS_ARMOR_STANDS = of("burns_armor_stands");
   TagKey AVOIDS_GUARDIAN_THORNS = of("avoids_guardian_thorns");
   TagKey ALWAYS_TRIGGERS_SILVERFISH = of("always_triggers_silverfish");
   TagKey ALWAYS_HURTS_ENDER_DRAGONS = of("always_hurts_ender_dragons");
   TagKey NO_KNOCKBACK = of("no_knockback");
   TagKey ALWAYS_KILLS_ARMOR_STANDS = of("always_kills_armor_stands");
   TagKey CAN_BREAK_ARMOR_STAND = of("can_break_armor_stand");
   TagKey BYPASSES_WOLF_ARMOR = of("bypasses_wolf_armor");
   TagKey IS_PLAYER_ATTACK = of("is_player_attack");
   TagKey BURN_FROM_STEPPING = of("burn_from_stepping");
   TagKey PANIC_CAUSES = of("panic_causes");
   TagKey PANIC_ENVIRONMENTAL_CAUSES = of("panic_environmental_causes");
   TagKey MACE_SMASH = of("mace_smash");

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla(id));
   }
}
