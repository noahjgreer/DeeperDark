package net.minecraft.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class Potions {
   public static final RegistryEntry WATER = register("water", new Potion("water", new StatusEffectInstance[0]));
   public static final RegistryEntry MUNDANE = register("mundane", new Potion("mundane", new StatusEffectInstance[0]));
   public static final RegistryEntry THICK = register("thick", new Potion("thick", new StatusEffectInstance[0]));
   public static final RegistryEntry AWKWARD = register("awkward", new Potion("awkward", new StatusEffectInstance[0]));
   public static final RegistryEntry NIGHT_VISION;
   public static final RegistryEntry LONG_NIGHT_VISION;
   public static final RegistryEntry INVISIBILITY;
   public static final RegistryEntry LONG_INVISIBILITY;
   public static final RegistryEntry LEAPING;
   public static final RegistryEntry LONG_LEAPING;
   public static final RegistryEntry STRONG_LEAPING;
   public static final RegistryEntry FIRE_RESISTANCE;
   public static final RegistryEntry LONG_FIRE_RESISTANCE;
   public static final RegistryEntry SWIFTNESS;
   public static final RegistryEntry LONG_SWIFTNESS;
   public static final RegistryEntry STRONG_SWIFTNESS;
   public static final RegistryEntry SLOWNESS;
   public static final RegistryEntry LONG_SLOWNESS;
   public static final RegistryEntry STRONG_SLOWNESS;
   public static final RegistryEntry TURTLE_MASTER;
   public static final RegistryEntry LONG_TURTLE_MASTER;
   public static final RegistryEntry STRONG_TURTLE_MASTER;
   public static final RegistryEntry WATER_BREATHING;
   public static final RegistryEntry LONG_WATER_BREATHING;
   public static final RegistryEntry HEALING;
   public static final RegistryEntry STRONG_HEALING;
   public static final RegistryEntry HARMING;
   public static final RegistryEntry STRONG_HARMING;
   public static final RegistryEntry POISON;
   public static final RegistryEntry LONG_POISON;
   public static final RegistryEntry STRONG_POISON;
   public static final RegistryEntry REGENERATION;
   public static final RegistryEntry LONG_REGENERATION;
   public static final RegistryEntry STRONG_REGENERATION;
   public static final RegistryEntry STRENGTH;
   public static final RegistryEntry LONG_STRENGTH;
   public static final RegistryEntry STRONG_STRENGTH;
   public static final RegistryEntry WEAKNESS;
   public static final RegistryEntry LONG_WEAKNESS;
   public static final RegistryEntry LUCK;
   public static final RegistryEntry SLOW_FALLING;
   public static final RegistryEntry LONG_SLOW_FALLING;
   public static final RegistryEntry WIND_CHARGED;
   public static final RegistryEntry WEAVING;
   public static final RegistryEntry OOZING;
   public static final RegistryEntry INFESTED;

   private static RegistryEntry register(String name, Potion potion) {
      return Registry.registerReference(Registries.POTION, (Identifier)Identifier.ofVanilla(name), potion);
   }

   public static RegistryEntry registerAndGetDefault(Registry registry) {
      return WATER;
   }

   static {
      NIGHT_VISION = register("night_vision", new Potion("night_vision", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600)}));
      LONG_NIGHT_VISION = register("long_night_vision", new Potion("night_vision", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600)}));
      INVISIBILITY = register("invisibility", new Potion("invisibility", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INVISIBILITY, 3600)}));
      LONG_INVISIBILITY = register("long_invisibility", new Potion("invisibility", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INVISIBILITY, 9600)}));
      LEAPING = register("leaping", new Potion("leaping", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600)}));
      LONG_LEAPING = register("long_leaping", new Potion("leaping", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.JUMP_BOOST, 9600)}));
      STRONG_LEAPING = register("strong_leaping", new Potion("leaping", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800, 1)}));
      FIRE_RESISTANCE = register("fire_resistance", new Potion("fire_resistance", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600)}));
      LONG_FIRE_RESISTANCE = register("long_fire_resistance", new Potion("fire_resistance", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600)}));
      SWIFTNESS = register("swiftness", new Potion("swiftness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SPEED, 3600)}));
      LONG_SWIFTNESS = register("long_swiftness", new Potion("swiftness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SPEED, 9600)}));
      STRONG_SWIFTNESS = register("strong_swiftness", new Potion("swiftness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SPEED, 1800, 1)}));
      SLOWNESS = register("slowness", new Potion("slowness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 1800)}));
      LONG_SLOWNESS = register("long_slowness", new Potion("slowness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 4800)}));
      STRONG_SLOWNESS = register("strong_slowness", new Potion("slowness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3)}));
      TURTLE_MASTER = register("turtle_master", new Potion("turtle_master", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3), new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 2)}));
      LONG_TURTLE_MASTER = register("long_turtle_master", new Potion("turtle_master", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 800, 3), new StatusEffectInstance(StatusEffects.RESISTANCE, 800, 2)}));
      STRONG_TURTLE_MASTER = register("strong_turtle_master", new Potion("turtle_master", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 5), new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 3)}));
      WATER_BREATHING = register("water_breathing", new Potion("water_breathing", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WATER_BREATHING, 3600)}));
      LONG_WATER_BREATHING = register("long_water_breathing", new Potion("water_breathing", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600)}));
      HEALING = register("healing", new Potion("healing", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1)}));
      STRONG_HEALING = register("strong_healing", new Potion("healing", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1)}));
      HARMING = register("harming", new Potion("harming", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1)}));
      STRONG_HARMING = register("strong_harming", new Potion("harming", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1)}));
      POISON = register("poison", new Potion("poison", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.POISON, 900)}));
      LONG_POISON = register("long_poison", new Potion("poison", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.POISON, 1800)}));
      STRONG_POISON = register("strong_poison", new Potion("poison", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.POISON, 432, 1)}));
      REGENERATION = register("regeneration", new Potion("regeneration", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.REGENERATION, 900)}));
      LONG_REGENERATION = register("long_regeneration", new Potion("regeneration", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.REGENERATION, 1800)}));
      STRONG_REGENERATION = register("strong_regeneration", new Potion("regeneration", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.REGENERATION, 450, 1)}));
      STRENGTH = register("strength", new Potion("strength", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.STRENGTH, 3600)}));
      LONG_STRENGTH = register("long_strength", new Potion("strength", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.STRENGTH, 9600)}));
      STRONG_STRENGTH = register("strong_strength", new Potion("strength", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.STRENGTH, 1800, 1)}));
      WEAKNESS = register("weakness", new Potion("weakness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WEAKNESS, 1800)}));
      LONG_WEAKNESS = register("long_weakness", new Potion("weakness", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WEAKNESS, 4800)}));
      LUCK = register("luck", new Potion("luck", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.LUCK, 6000)}));
      SLOW_FALLING = register("slow_falling", new Potion("slow_falling", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800)}));
      LONG_SLOW_FALLING = register("long_slow_falling", new Potion("slow_falling", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.SLOW_FALLING, 4800)}));
      WIND_CHARGED = register("wind_charged", new Potion("wind_charged", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WIND_CHARGED, 3600)}));
      WEAVING = register("weaving", new Potion("weaving", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.WEAVING, 3600)}));
      OOZING = register("oozing", new Potion("oozing", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.OOZING, 3600)}));
      INFESTED = register("infested", new Potion("infested", new StatusEffectInstance[]{new StatusEffectInstance(StatusEffects.INFESTED, 3600)}));
   }
}
