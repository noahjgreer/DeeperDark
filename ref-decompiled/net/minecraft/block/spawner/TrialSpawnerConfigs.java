package net.minecraft.block.spawner;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerConfigs {
   private static final ConfigKeyPair BREEZE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/breeze");
   private static final ConfigKeyPair MELEE_HUSK = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/husk");
   private static final ConfigKeyPair MELEE_SPIDER = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/spider");
   private static final ConfigKeyPair MELEE_ZOMBIE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/melee/zombie");
   private static final ConfigKeyPair RANGED_POISON_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/poison_skeleton");
   private static final ConfigKeyPair RANGED_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/skeleton");
   private static final ConfigKeyPair RANGED_STRAY = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/ranged/stray");
   private static final ConfigKeyPair SLOW_RANGED_POISON_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/slow_ranged/poison_skeleton");
   private static final ConfigKeyPair SLOW_RANGED_SKELETON = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/slow_ranged/skeleton");
   private static final ConfigKeyPair SLOW_RANGED_STRAY = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/slow_ranged/stray");
   private static final ConfigKeyPair SMALL_MELEE_BABY_ZOMBIE = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/baby_zombie");
   private static final ConfigKeyPair SMALL_MELEE_CAVE_SPIDER = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/cave_spider");
   private static final ConfigKeyPair SMALL_MELEE_SILVERFISH = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/silverfish");
   private static final ConfigKeyPair SMALL_MELEE_SLIME = TrialSpawnerConfigs.ConfigKeyPair.of("trial_chamber/small_melee/slime");

   public static void bootstrap(Registerable registry) {
      register(registry, BREEZE, TrialSpawnerConfig.builder().simultaneousMobs(1.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(2.0F).totalMobsAddedPerPlayer(1.0F).spawnPotentials(Pool.of((Object)createEntry(EntityType.BREEZE))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(4.0F).totalMobsAddedPerPlayer(1.0F).spawnPotentials(Pool.of((Object)createEntry(EntityType.BREEZE))).lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
      register(registry, MELEE_HUSK, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.HUSK))).build(), genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.HUSK, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
      register(registry, MELEE_SPIDER, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.SPIDER))).build(), ominousMeleeBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.SPIDER))).lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
      register(registry, MELEE_ZOMBIE, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.ZOMBIE))).build(), genericBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.ZOMBIE, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
      register(registry, RANGED_POISON_SKELETON, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.BOGGED))).build(), genericBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, RANGED_SKELETON, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.SKELETON))).build(), genericBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, RANGED_STRAY, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.STRAY))).build(), genericBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, SLOW_RANGED_POISON_SKELETON, slowRangedBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.BOGGED))).build(), slowRangedBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.BOGGED, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, SLOW_RANGED_SKELETON, slowRangedBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.SKELETON))).build(), slowRangedBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.SKELETON, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, SLOW_RANGED_STRAY, slowRangedBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.STRAY))).build(), slowRangedBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.STRAY, LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
      register(registry, SMALL_MELEE_BABY_ZOMBIE, TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).spawnPotentials(Pool.of((Object)createEntry(EntityType.ZOMBIE, (nbt) -> {
         nbt.putBoolean("IsBaby", true);
      }, (RegistryKey)null))).build(), TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.ZOMBIE, (nbt) -> {
         nbt.putBoolean("IsBaby", true);
      }, LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
      register(registry, SMALL_MELEE_CAVE_SPIDER, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.CAVE_SPIDER))).build(), ominousMeleeBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.CAVE_SPIDER))).build());
      register(registry, SMALL_MELEE_SILVERFISH, genericBuilder().spawnPotentials(Pool.of((Object)createEntry(EntityType.SILVERFISH))).build(), ominousMeleeBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)createEntry(EntityType.SILVERFISH))).build());
      register(registry, SMALL_MELEE_SLIME, genericBuilder().spawnPotentials(Pool.builder().add(createEntry(EntityType.SLIME, (nbt) -> {
         nbt.putByte("Size", (byte)1);
      }), 3).add(createEntry(EntityType.SLIME, (nbt) -> {
         nbt.putByte("Size", (byte)2);
      }), 1).build()).build(), ominousMeleeBuilder().lootTablesToEject(Pool.builder().add(LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add(LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.builder().add(createEntry(EntityType.SLIME, (nbt) -> {
         nbt.putByte("Size", (byte)1);
      }), 3).add(createEntry(EntityType.SLIME, (nbt) -> {
         nbt.putByte("Size", (byte)2);
      }), 1).build()).build());
   }

   private static MobSpawnerEntry createEntry(EntityType entityType) {
      return createEntry(entityType, (nbt) -> {
      }, (RegistryKey)null);
   }

   private static MobSpawnerEntry createEntry(EntityType entityType, Consumer nbtConsumer) {
      return createEntry(entityType, nbtConsumer, (RegistryKey)null);
   }

   private static MobSpawnerEntry createEntry(EntityType entityType, RegistryKey equipmentTable) {
      return createEntry(entityType, (nbt) -> {
      }, equipmentTable);
   }

   private static MobSpawnerEntry createEntry(EntityType entityType, Consumer nbtConsumer, @Nullable RegistryKey equipmentTable) {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
      nbtConsumer.accept(nbtCompound);
      Optional optional = Optional.ofNullable(equipmentTable).map((lootTable) -> {
         return new EquipmentTable(lootTable, 0.0F);
      });
      return new MobSpawnerEntry(nbtCompound, Optional.empty(), optional);
   }

   private static void register(Registerable registry, ConfigKeyPair configPair, TrialSpawnerConfig normalConfig, TrialSpawnerConfig ominousConfig) {
      registry.register(configPair.normal, normalConfig);
      registry.register(configPair.ominous, ominousConfig);
   }

   static RegistryKey keyOf(String id) {
      return RegistryKey.of(RegistryKeys.TRIAL_SPAWNER, Identifier.ofVanilla(id));
   }

   private static TrialSpawnerConfig.Builder ominousMeleeBuilder() {
      return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20).totalMobs(12.0F);
   }

   private static TrialSpawnerConfig.Builder slowRangedBuilder() {
      return TrialSpawnerConfig.builder().simultaneousMobs(4.0F).simultaneousMobsAddedPerPlayer(2.0F).ticksBetweenSpawn(160);
   }

   private static TrialSpawnerConfig.Builder genericBuilder() {
      return TrialSpawnerConfig.builder().simultaneousMobs(3.0F).simultaneousMobsAddedPerPlayer(0.5F).ticksBetweenSpawn(20);
   }

   static record ConfigKeyPair(RegistryKey normal, RegistryKey ominous) {
      final RegistryKey normal;
      final RegistryKey ominous;

      private ConfigKeyPair(RegistryKey registryKey, RegistryKey registryKey2) {
         this.normal = registryKey;
         this.ominous = registryKey2;
      }

      public static ConfigKeyPair of(String id) {
         return new ConfigKeyPair(TrialSpawnerConfigs.keyOf(id + "/normal"), TrialSpawnerConfigs.keyOf(id + "/ominous"));
      }

      public RegistryKey normal() {
         return this.normal;
      }

      public RegistryKey ominous() {
         return this.ominous;
      }
   }
}
