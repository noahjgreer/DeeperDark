/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.spawner.MobSpawnerEntry
 *  net.minecraft.block.spawner.TrialSpawnerConfig
 *  net.minecraft.block.spawner.TrialSpawnerConfig$Builder
 *  net.minecraft.block.spawner.TrialSpawnerConfigs
 *  net.minecraft.block.spawner.TrialSpawnerConfigs$ConfigKeyPair
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentTable
 *  net.minecraft.loot.LootTable
 *  net.minecraft.loot.LootTables
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.Registerable
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.collection.Pool
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.spawner;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerConfigs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TrialSpawnerConfigs {
    private static final ConfigKeyPair BREEZE = ConfigKeyPair.of((String)"trial_chamber/breeze");
    private static final ConfigKeyPair MELEE_HUSK = ConfigKeyPair.of((String)"trial_chamber/melee/husk");
    private static final ConfigKeyPair MELEE_SPIDER = ConfigKeyPair.of((String)"trial_chamber/melee/spider");
    private static final ConfigKeyPair MELEE_ZOMBIE = ConfigKeyPair.of((String)"trial_chamber/melee/zombie");
    private static final ConfigKeyPair RANGED_POISON_SKELETON = ConfigKeyPair.of((String)"trial_chamber/ranged/poison_skeleton");
    private static final ConfigKeyPair RANGED_SKELETON = ConfigKeyPair.of((String)"trial_chamber/ranged/skeleton");
    private static final ConfigKeyPair RANGED_STRAY = ConfigKeyPair.of((String)"trial_chamber/ranged/stray");
    private static final ConfigKeyPair SLOW_RANGED_POISON_SKELETON = ConfigKeyPair.of((String)"trial_chamber/slow_ranged/poison_skeleton");
    private static final ConfigKeyPair SLOW_RANGED_SKELETON = ConfigKeyPair.of((String)"trial_chamber/slow_ranged/skeleton");
    private static final ConfigKeyPair SLOW_RANGED_STRAY = ConfigKeyPair.of((String)"trial_chamber/slow_ranged/stray");
    private static final ConfigKeyPair SMALL_MELEE_BABY_ZOMBIE = ConfigKeyPair.of((String)"trial_chamber/small_melee/baby_zombie");
    private static final ConfigKeyPair SMALL_MELEE_CAVE_SPIDER = ConfigKeyPair.of((String)"trial_chamber/small_melee/cave_spider");
    private static final ConfigKeyPair SMALL_MELEE_SILVERFISH = ConfigKeyPair.of((String)"trial_chamber/small_melee/silverfish");
    private static final ConfigKeyPair SMALL_MELEE_SLIME = ConfigKeyPair.of((String)"trial_chamber/small_melee/slime");

    public static void bootstrap(Registerable<TrialSpawnerConfig> registry) {
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)BREEZE, (TrialSpawnerConfig)TrialSpawnerConfig.builder().simultaneousMobs(1.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(2.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BREEZE))).build(), (TrialSpawnerConfig)TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(4.0f).totalMobsAddedPerPlayer(1.0f).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BREEZE))).lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)MELEE_HUSK, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.HUSK))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.HUSK, (RegistryKey)LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)MELEE_SPIDER, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SPIDER))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.ominousMeleeBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SPIDER))).lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)MELEE_ZOMBIE, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.ZOMBIE))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.ZOMBIE, (RegistryKey)LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)RANGED_POISON_SKELETON, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BOGGED))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BOGGED, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)RANGED_SKELETON, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SKELETON))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SKELETON, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)RANGED_STRAY, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.STRAY))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.STRAY, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SLOW_RANGED_POISON_SKELETON, (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BOGGED))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.BOGGED, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SLOW_RANGED_SKELETON, (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SKELETON))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SKELETON, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SLOW_RANGED_STRAY, (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.STRAY))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.slowRangedBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.STRAY, (RegistryKey)LootTables.TRIAL_CHAMBER_RANGED_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SMALL_MELEE_BABY_ZOMBIE, (TrialSpawnerConfig)TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.ZOMBIE, (T nbt) -> nbt.putBoolean("IsBaby", true), null))).build(), (TrialSpawnerConfig)TrialSpawnerConfig.builder().simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.ZOMBIE, (T nbt) -> nbt.putBoolean("IsBaby", true), (RegistryKey)LootTables.TRIAL_CHAMBER_MELEE_EQUIPMENT))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SMALL_MELEE_CAVE_SPIDER, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.CAVE_SPIDER))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.CAVE_SPIDER))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SMALL_MELEE_SILVERFISH, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SILVERFISH))).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.of((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SILVERFISH))).build());
        TrialSpawnerConfigs.register(registry, (ConfigKeyPair)SMALL_MELEE_SLIME, (TrialSpawnerConfig)TrialSpawnerConfigs.genericBuilder().spawnPotentials(Pool.builder().add((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SLIME, (T nbt) -> nbt.putByte("Size", (byte)1)), 3).add((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SLIME, (T nbt) -> nbt.putByte("Size", (byte)2)), 1).build()).build(), (TrialSpawnerConfig)TrialSpawnerConfigs.ominousMeleeBuilder().lootTablesToEject(Pool.builder().add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER, 3).add((Object)LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER, 7).build()).spawnPotentials(Pool.builder().add((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SLIME, (T nbt) -> nbt.putByte("Size", (byte)1)), 3).add((Object)TrialSpawnerConfigs.createEntry((EntityType)EntityType.SLIME, (T nbt) -> nbt.putByte("Size", (byte)2)), 1).build()).build());
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType) {
        return TrialSpawnerConfigs.createEntry(entityType, (T nbt) -> {}, null);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer) {
        return TrialSpawnerConfigs.createEntry(entityType, nbtConsumer, null);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, RegistryKey<LootTable> equipmentTable) {
        return TrialSpawnerConfigs.createEntry(entityType, (T nbt) -> {}, equipmentTable);
    }

    private static <T extends Entity> MobSpawnerEntry createEntry(EntityType<T> entityType, Consumer<NbtCompound> nbtConsumer, @Nullable RegistryKey<LootTable> equipmentTable) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        nbtConsumer.accept(nbtCompound);
        Optional<EquipmentTable> optional = Optional.ofNullable(equipmentTable).map(lootTable -> new EquipmentTable(lootTable, 0.0f));
        return new MobSpawnerEntry(nbtCompound, Optional.empty(), optional);
    }

    private static void register(Registerable<TrialSpawnerConfig> registry, ConfigKeyPair configPair, TrialSpawnerConfig normalConfig, TrialSpawnerConfig ominousConfig) {
        registry.register(configPair.normal, (Object)normalConfig);
        registry.register(configPair.ominous, (Object)ominousConfig);
    }

    static RegistryKey<TrialSpawnerConfig> keyOf(String id) {
        return RegistryKey.of((RegistryKey)RegistryKeys.TRIAL_SPAWNER, (Identifier)Identifier.ofVanilla((String)id));
    }

    private static TrialSpawnerConfig.Builder ominousMeleeBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20).totalMobs(12.0f);
    }

    private static TrialSpawnerConfig.Builder slowRangedBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(4.0f).simultaneousMobsAddedPerPlayer(2.0f).ticksBetweenSpawn(160);
    }

    private static TrialSpawnerConfig.Builder genericBuilder() {
        return TrialSpawnerConfig.builder().simultaneousMobs(3.0f).simultaneousMobsAddedPerPlayer(0.5f).ticksBetweenSpawn(20);
    }
}

