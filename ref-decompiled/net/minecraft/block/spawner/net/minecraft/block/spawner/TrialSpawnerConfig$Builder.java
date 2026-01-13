/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.Pool;

public static class TrialSpawnerConfig.Builder {
    private int spawnRange = 4;
    private float totalMobs = 6.0f;
    private float simultaneousMobs = 2.0f;
    private float totalMobsAddedPerPlayer = 2.0f;
    private float simultaneousMobsAddedPerPlayer = 1.0f;
    private int ticksBetweenSpawn = 40;
    private Pool<MobSpawnerEntry> spawnPotentials = Pool.empty();
    private Pool<RegistryKey<LootTable>> lootTablesToEject = Pool.builder().add(LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER).add(LootTables.TRIAL_CHAMBER_KEY_SPAWNER).build();
    private RegistryKey<LootTable> itemsToDropWhenOminous = LootTables.TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER;

    public TrialSpawnerConfig.Builder spawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
        return this;
    }

    public TrialSpawnerConfig.Builder totalMobs(float totalMobs) {
        this.totalMobs = totalMobs;
        return this;
    }

    public TrialSpawnerConfig.Builder simultaneousMobs(float simultaneousMobs) {
        this.simultaneousMobs = simultaneousMobs;
        return this;
    }

    public TrialSpawnerConfig.Builder totalMobsAddedPerPlayer(float totalMobsAddedPerPlayer) {
        this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
        return this;
    }

    public TrialSpawnerConfig.Builder simultaneousMobsAddedPerPlayer(float simultaneousMobsAddedPerPlayer) {
        this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
        return this;
    }

    public TrialSpawnerConfig.Builder ticksBetweenSpawn(int ticksBetweenSpawn) {
        this.ticksBetweenSpawn = ticksBetweenSpawn;
        return this;
    }

    public TrialSpawnerConfig.Builder spawnPotentials(Pool<MobSpawnerEntry> spawnPotentials) {
        this.spawnPotentials = spawnPotentials;
        return this;
    }

    public TrialSpawnerConfig.Builder lootTablesToEject(Pool<RegistryKey<LootTable>> lootTablesToEject) {
        this.lootTablesToEject = lootTablesToEject;
        return this;
    }

    public TrialSpawnerConfig.Builder itemsToDropWhenOminous(RegistryKey<LootTable> itemsToDropWhenOminous) {
        this.itemsToDropWhenOminous = itemsToDropWhenOminous;
        return this;
    }

    public TrialSpawnerConfig build() {
        return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentials, this.lootTablesToEject, this.itemsToDropWhenOminous);
    }
}
