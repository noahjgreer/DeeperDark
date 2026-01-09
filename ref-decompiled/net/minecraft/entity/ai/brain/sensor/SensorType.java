package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.ArmadilloBrain;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.CamelBrain;
import net.minecraft.entity.passive.FrogBrain;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SensorType {
   public static final SensorType DUMMY = register("dummy", DummySensor::new);
   public static final SensorType NEAREST_ITEMS = register("nearest_items", NearestItemsSensor::new);
   public static final SensorType NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitiesSensor::new);
   public static final SensorType NEAREST_PLAYERS = register("nearest_players", NearestPlayersSensor::new);
   public static final SensorType NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
   public static final SensorType HURT_BY = register("hurt_by", HurtBySensor::new);
   public static final SensorType VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
   public static final SensorType VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
   public static final SensorType SECONDARY_POIS = register("secondary_pois", SecondaryPointsOfInterestSensor::new);
   public static final SensorType GOLEM_DETECTED = register("golem_detected", GolemLastSeenSensor::new);
   public static final SensorType ARMADILLO_SCARE_DETECTED = register("armadillo_scare_detected", () -> {
      return new ArmadilloScareDetectedSensor(5, ArmadilloEntity::isEntityThreatening, ArmadilloEntity::canRollUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80);
   });
   public static final SensorType PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinSpecificSensor::new);
   public static final SensorType PIGLIN_BRUTE_SPECIFIC_SENSOR = register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
   public static final SensorType HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinSpecificSensor::new);
   public static final SensorType NEAREST_ADULT = register("nearest_adult", NearestVisibleAdultSensor::new);
   public static final SensorType NEAREST_ADULT_ANY_TYPE = register("nearest_adult_any_type", NearestFollowableFriendlyMobSensor::new);
   public static final SensorType AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
   public static final SensorType AXOLOTL_TEMPTATIONS = register("axolotl_temptations", () -> {
      return new TemptationsSensor(AxolotlBrain.getTemptItemPredicate());
   });
   public static final SensorType GOAT_TEMPTATIONS = register("goat_temptations", () -> {
      return new TemptationsSensor(GoatBrain.getTemptItemPredicate());
   });
   public static final SensorType FROG_TEMPTATIONS = register("frog_temptations", () -> {
      return new TemptationsSensor(FrogBrain.getTemptItemPredicate());
   });
   public static final SensorType CAMEL_TEMPTATIONS = register("camel_temptations", () -> {
      return new TemptationsSensor(CamelBrain.getTemptItemPredicate());
   });
   public static final SensorType ARMADILLO_TEMPTATIONS = register("armadillo_temptations", () -> {
      return new TemptationsSensor(ArmadilloBrain.getTemptItemPredicate());
   });
   public static final SensorType HAPPY_GHAST_TEMPTATIONS = register("happy_ghast_temptations", () -> {
      return new TemptationsSensor(HappyGhastEntity.FOOD_PREDICATE);
   });
   public static final SensorType FROG_ATTACKABLES = register("frog_attackables", FrogAttackablesSensor::new);
   public static final SensorType IS_IN_WATER = register("is_in_water", IsInWaterSensor::new);
   public static final SensorType WARDEN_ENTITY_SENSOR = register("warden_entity_sensor", WardenAttackablesSensor::new);
   public static final SensorType SNIFFER_TEMPTATIONS = register("sniffer_temptations", () -> {
      return new TemptationsSensor(SnifferBrain.getTemptItemPredicate());
   });
   public static final SensorType BREEZE_ATTACK_ENTITY_SENSOR = register("breeze_attack_entity_sensor", BreezeAttackablesSensor::new);
   private final Supplier factory;

   public SensorType(Supplier factory) {
      this.factory = factory;
   }

   public Sensor create() {
      return (Sensor)this.factory.get();
   }

   private static SensorType register(String id, Supplier factory) {
      return (SensorType)Registry.register(Registries.SENSOR_TYPE, (Identifier)Identifier.ofVanilla(id), new SensorType(factory));
   }
}
