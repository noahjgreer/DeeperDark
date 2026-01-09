package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EntityTypeTags {
   TagKey SKELETONS = of("skeletons");
   TagKey ZOMBIES = of("zombies");
   TagKey RAIDERS = of("raiders");
   TagKey UNDEAD = of("undead");
   TagKey BEEHIVE_INHABITORS = of("beehive_inhabitors");
   TagKey ARROWS = of("arrows");
   TagKey IMPACT_PROJECTILES = of("impact_projectiles");
   TagKey POWDER_SNOW_WALKABLE_MOBS = of("powder_snow_walkable_mobs");
   TagKey AXOLOTL_ALWAYS_HOSTILES = of("axolotl_always_hostiles");
   TagKey AXOLOTL_HUNT_TARGETS = of("axolotl_hunt_targets");
   TagKey FREEZE_IMMUNE_ENTITY_TYPES = of("freeze_immune_entity_types");
   TagKey FREEZE_HURTS_EXTRA_TYPES = of("freeze_hurts_extra_types");
   TagKey CAN_BREATHE_UNDER_WATER = of("can_breathe_under_water");
   TagKey FROG_FOOD = of("frog_food");
   TagKey FALL_DAMAGE_IMMUNE = of("fall_damage_immune");
   TagKey DISMOUNTS_UNDERWATER = of("dismounts_underwater");
   TagKey NON_CONTROLLING_RIDER = of("non_controlling_rider");
   TagKey DEFLECTS_PROJECTILES = of("deflects_projectiles");
   TagKey CAN_TURN_IN_BOATS = of("can_turn_in_boats");
   TagKey ILLAGER = of("illager");
   TagKey AQUATIC = of("aquatic");
   TagKey ARTHROPOD = of("arthropod");
   TagKey IGNORES_POISON_AND_REGEN = of("ignores_poison_and_regen");
   TagKey INVERTED_HEALING_AND_HARM = of("inverted_healing_and_harm");
   TagKey WITHER_FRIENDS = of("wither_friends");
   TagKey ILLAGER_FRIENDS = of("illager_friends");
   TagKey NOT_SCARY_FOR_PUFFERFISH = of("not_scary_for_pufferfish");
   TagKey SENSITIVE_TO_IMPALING = of("sensitive_to_impaling");
   TagKey SENSITIVE_TO_BANE_OF_ARTHROPODS = of("sensitive_to_bane_of_arthropods");
   TagKey SENSITIVE_TO_SMITE = of("sensitive_to_smite");
   TagKey NO_ANGER_FROM_WIND_CHARGE = of("no_anger_from_wind_charge");
   TagKey IMMUNE_TO_OOZING = of("immune_to_oozing");
   TagKey IMMUNE_TO_INFESTED = of("immune_to_infested");
   TagKey REDIRECTABLE_PROJECTILE = of("redirectable_projectile");
   TagKey BOAT = of("boat");
   TagKey CAN_EQUIP_SADDLE = of("can_equip_saddle");
   TagKey CAN_EQUIP_HARNESS = of("can_equip_harness");
   TagKey CAN_WEAR_HORSE_ARMOR = of("can_wear_horse_armor");
   TagKey FOLLOWABLE_FRIENDLY_MOBS = of("followable_friendly_mobs");

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
   }
}
