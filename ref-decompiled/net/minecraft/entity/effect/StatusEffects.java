package net.minecraft.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class StatusEffects {
   private static final int DARKNESS_PADDING_DURATION = 22;
   public static final RegistryEntry SPEED;
   public static final RegistryEntry SLOWNESS;
   public static final RegistryEntry HASTE;
   public static final RegistryEntry MINING_FATIGUE;
   public static final RegistryEntry STRENGTH;
   public static final RegistryEntry INSTANT_HEALTH;
   public static final RegistryEntry INSTANT_DAMAGE;
   public static final RegistryEntry JUMP_BOOST;
   public static final RegistryEntry NAUSEA;
   public static final RegistryEntry REGENERATION;
   public static final RegistryEntry RESISTANCE;
   public static final RegistryEntry FIRE_RESISTANCE;
   public static final RegistryEntry WATER_BREATHING;
   public static final RegistryEntry INVISIBILITY;
   public static final RegistryEntry BLINDNESS;
   public static final RegistryEntry NIGHT_VISION;
   public static final RegistryEntry HUNGER;
   public static final RegistryEntry WEAKNESS;
   public static final RegistryEntry POISON;
   public static final RegistryEntry WITHER;
   public static final RegistryEntry HEALTH_BOOST;
   public static final RegistryEntry ABSORPTION;
   public static final RegistryEntry SATURATION;
   public static final RegistryEntry GLOWING;
   public static final RegistryEntry LEVITATION;
   public static final RegistryEntry LUCK;
   public static final RegistryEntry UNLUCK;
   public static final RegistryEntry SLOW_FALLING;
   public static final RegistryEntry CONDUIT_POWER;
   public static final RegistryEntry DOLPHINS_GRACE;
   public static final RegistryEntry BAD_OMEN;
   public static final RegistryEntry HERO_OF_THE_VILLAGE;
   public static final RegistryEntry DARKNESS;
   public static final RegistryEntry TRIAL_OMEN;
   public static final RegistryEntry RAID_OMEN;
   public static final RegistryEntry WIND_CHARGED;
   public static final RegistryEntry WEAVING;
   public static final RegistryEntry OOZING;
   public static final RegistryEntry INFESTED;

   private static RegistryEntry register(String id, StatusEffect statusEffect) {
      return Registry.registerReference(Registries.STATUS_EFFECT, (Identifier)Identifier.ofVanilla(id), statusEffect);
   }

   public static RegistryEntry registerAndGetDefault(Registry registry) {
      return SPEED;
   }

   static {
      SPEED = register("speed", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 3402751)).addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.ofVanilla("effect.speed"), 0.20000000298023224, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      SLOWNESS = register("slowness", (new StatusEffect(StatusEffectCategory.HARMFUL, 9154528)).addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.ofVanilla("effect.slowness"), -0.15000000596046448, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      HASTE = register("haste", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 14270531)).addAttributeModifier(EntityAttributes.ATTACK_SPEED, Identifier.ofVanilla("effect.haste"), 0.10000000149011612, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      MINING_FATIGUE = register("mining_fatigue", (new StatusEffect(StatusEffectCategory.HARMFUL, 4866583)).addAttributeModifier(EntityAttributes.ATTACK_SPEED, Identifier.ofVanilla("effect.mining_fatigue"), -0.10000000149011612, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      STRENGTH = register("strength", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 16762624)).addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, Identifier.ofVanilla("effect.strength"), 3.0, EntityAttributeModifier.Operation.ADD_VALUE));
      INSTANT_HEALTH = register("instant_health", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179, false));
      INSTANT_DAMAGE = register("instant_damage", new InstantHealthOrDamageStatusEffect(StatusEffectCategory.HARMFUL, 11101546, true));
      JUMP_BOOST = register("jump_boost", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 16646020)).addAttributeModifier(EntityAttributes.SAFE_FALL_DISTANCE, Identifier.ofVanilla("effect.jump_boost"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE));
      NAUSEA = register("nausea", (new StatusEffect(StatusEffectCategory.HARMFUL, 5578058)).fadeTicks(150, 20, 60));
      REGENERATION = register("regeneration", new RegenerationStatusEffect(StatusEffectCategory.BENEFICIAL, 13458603));
      RESISTANCE = register("resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 9520880));
      FIRE_RESISTANCE = register("fire_resistance", new StatusEffect(StatusEffectCategory.BENEFICIAL, 16750848));
      WATER_BREATHING = register("water_breathing", new StatusEffect(StatusEffectCategory.BENEFICIAL, 10017472));
      INVISIBILITY = register("invisibility", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 16185078)).addAttributeModifier(EntityAttributes.WAYPOINT_TRANSMIT_RANGE, Identifier.ofVanilla("effect.waypoint_transmit_range_hide"), -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      BLINDNESS = register("blindness", new StatusEffect(StatusEffectCategory.HARMFUL, 2039587));
      NIGHT_VISION = register("night_vision", new StatusEffect(StatusEffectCategory.BENEFICIAL, 12779366));
      HUNGER = register("hunger", new HungerStatusEffect(StatusEffectCategory.HARMFUL, 5797459));
      WEAKNESS = register("weakness", (new StatusEffect(StatusEffectCategory.HARMFUL, 4738376)).addAttributeModifier(EntityAttributes.ATTACK_DAMAGE, Identifier.ofVanilla("effect.weakness"), -4.0, EntityAttributeModifier.Operation.ADD_VALUE));
      POISON = register("poison", new PoisonStatusEffect(StatusEffectCategory.HARMFUL, 8889187));
      WITHER = register("wither", new WitherStatusEffect(StatusEffectCategory.HARMFUL, 7561558));
      HEALTH_BOOST = register("health_boost", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 16284963)).addAttributeModifier(EntityAttributes.MAX_HEALTH, Identifier.ofVanilla("effect.health_boost"), 4.0, EntityAttributeModifier.Operation.ADD_VALUE));
      ABSORPTION = register("absorption", (new AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 2445989)).addAttributeModifier(EntityAttributes.MAX_ABSORPTION, Identifier.ofVanilla("effect.absorption"), 4.0, EntityAttributeModifier.Operation.ADD_VALUE));
      SATURATION = register("saturation", new SaturationStatusEffect(StatusEffectCategory.BENEFICIAL, 16262179));
      GLOWING = register("glowing", new StatusEffect(StatusEffectCategory.NEUTRAL, 9740385));
      LEVITATION = register("levitation", new StatusEffect(StatusEffectCategory.HARMFUL, 13565951));
      LUCK = register("luck", (new StatusEffect(StatusEffectCategory.BENEFICIAL, 5882118)).addAttributeModifier(EntityAttributes.LUCK, Identifier.ofVanilla("effect.luck"), 1.0, EntityAttributeModifier.Operation.ADD_VALUE));
      UNLUCK = register("unluck", (new StatusEffect(StatusEffectCategory.HARMFUL, 12624973)).addAttributeModifier(EntityAttributes.LUCK, Identifier.ofVanilla("effect.unluck"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE));
      SLOW_FALLING = register("slow_falling", new StatusEffect(StatusEffectCategory.BENEFICIAL, 15978425));
      CONDUIT_POWER = register("conduit_power", new StatusEffect(StatusEffectCategory.BENEFICIAL, 1950417));
      DOLPHINS_GRACE = register("dolphins_grace", new StatusEffect(StatusEffectCategory.BENEFICIAL, 8954814));
      BAD_OMEN = register("bad_omen", (new BadOmenStatusEffect(StatusEffectCategory.NEUTRAL, 745784)).applySound(SoundEvents.EVENT_MOB_EFFECT_BAD_OMEN));
      HERO_OF_THE_VILLAGE = register("hero_of_the_village", new StatusEffect(StatusEffectCategory.BENEFICIAL, 4521796));
      DARKNESS = register("darkness", (new StatusEffect(StatusEffectCategory.HARMFUL, 2696993)).fadeTicks(22));
      TRIAL_OMEN = register("trial_omen", (new StatusEffect(StatusEffectCategory.NEUTRAL, 1484454, ParticleTypes.TRIAL_OMEN)).applySound(SoundEvents.EVENT_MOB_EFFECT_TRIAL_OMEN));
      RAID_OMEN = register("raid_omen", (new RaidOmenStatusEffect(StatusEffectCategory.NEUTRAL, 14565464, ParticleTypes.RAID_OMEN)).applySound(SoundEvents.EVENT_MOB_EFFECT_RAID_OMEN));
      WIND_CHARGED = register("wind_charged", new WindChargedStatusEffect(StatusEffectCategory.HARMFUL, 12438015));
      WEAVING = register("weaving", new WeavingStatusEffect(StatusEffectCategory.HARMFUL, 7891290, (random) -> {
         return MathHelper.nextBetween(random, 2, 3);
      }));
      OOZING = register("oozing", new OozingStatusEffect(StatusEffectCategory.HARMFUL, 10092451, (random) -> {
         return 2;
      }));
      INFESTED = register("infested", new InfestedStatusEffect(StatusEffectCategory.HARMFUL, 9214860, 0.1F, (random) -> {
         return MathHelper.nextBetween(random, 1, 2);
      }));
   }
}
