/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.spawner.MobSpawnerEntry
 *  net.minecraft.block.spawner.TrialSpawnerConfig
 *  net.minecraft.block.spawner.TrialSpawnerConfig$Builder
 *  net.minecraft.entity.EntityType
 *  net.minecraft.loot.LootTable
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryElementCodec
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.util.collection.Pool
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.Pool;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, Pool<MobSpawnerEntry> spawnPotentials, Pool<RegistryKey<LootTable>> lootTablesToEject, RegistryKey<LootTable> itemsToDropWhenOminous) {
    private final int spawnRange;
    private final float totalMobs;
    private final float simultaneousMobs;
    private final float totalMobsAddedPerPlayer;
    private final float simultaneousMobsAddedPerPlayer;
    private final int ticksBetweenSpawn;
    private final Pool<MobSpawnerEntry> spawnPotentials;
    private final Pool<RegistryKey<LootTable>> lootTablesToEject;
    private final RegistryKey<LootTable> itemsToDropWhenOminous;
    public static final TrialSpawnerConfig DEFAULT = TrialSpawnerConfig.builder().build();
    public static final Codec<TrialSpawnerConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)1, (int)128).optionalFieldOf("spawn_range", (Object)TrialSpawnerConfig.DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).optionalFieldOf("total_mobs", (Object)Float.valueOf(TrialSpawnerConfig.DEFAULT.totalMobs)).forGetter(TrialSpawnerConfig::totalMobs), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", (Object)Float.valueOf(TrialSpawnerConfig.DEFAULT.simultaneousMobs)).forGetter(TrialSpawnerConfig::simultaneousMobs), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).optionalFieldOf("total_mobs_added_per_player", (Object)Float.valueOf(TrialSpawnerConfig.DEFAULT.totalMobsAddedPerPlayer)).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs_added_per_player", (Object)Float.valueOf(TrialSpawnerConfig.DEFAULT.simultaneousMobsAddedPerPlayer)).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", (Object)TrialSpawnerConfig.DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), (App)MobSpawnerEntry.DATA_POOL_CODEC.optionalFieldOf("spawn_potentials", (Object)Pool.empty()).forGetter(TrialSpawnerConfig::spawnPotentials), (App)Pool.createCodec((Codec)LootTable.TABLE_KEY).optionalFieldOf("loot_tables_to_eject", (Object)TrialSpawnerConfig.DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), (App)LootTable.TABLE_KEY.optionalFieldOf("items_to_drop_when_ominous", (Object)TrialSpawnerConfig.DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply((Applicative)instance, TrialSpawnerConfig::new));
    public static final Codec<RegistryEntry<TrialSpawnerConfig>> ENTRY_CODEC = RegistryElementCodec.of((RegistryKey)RegistryKeys.TRIAL_SPAWNER, (Codec)CODEC);

    public TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, Pool<MobSpawnerEntry> spawnPotentials, Pool<RegistryKey<LootTable>> lootTablesToEject, RegistryKey<LootTable> itemsToDropWhenOminous) {
        this.spawnRange = spawnRange;
        this.totalMobs = totalMobs;
        this.simultaneousMobs = simultaneousMobs;
        this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
        this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
        this.ticksBetweenSpawn = ticksBetweenSpawn;
        this.spawnPotentials = spawnPotentials;
        this.lootTablesToEject = lootTablesToEject;
        this.itemsToDropWhenOminous = itemsToDropWhenOminous;
    }

    public int getTotalMobs(int additionalPlayers) {
        return (int)Math.floor(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers);
    }

    public int getSimultaneousMobs(int additionalPlayers) {
        return (int)Math.floor(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers);
    }

    public long getCooldownLength() {
        return 160L;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TrialSpawnerConfig withSpawnPotential(EntityType<?> entityType) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(entityType).toString());
        MobSpawnerEntry mobSpawnerEntry = new MobSpawnerEntry(nbtCompound, Optional.empty(), Optional.empty());
        return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, Pool.of((Object)mobSpawnerEntry), this.lootTablesToEject, this.itemsToDropWhenOminous);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrialSpawnerConfig.class, "spawnRange;totalMobs;simultaneousMobs;totalMobsAddedPerPlayer;simultaneousMobsAddedPerPlayer;ticksBetweenSpawn;spawnPotentialsDefinition;lootTablesToEject;itemsToDropWhenOminous", "spawnRange", "totalMobs", "simultaneousMobs", "totalMobsAddedPerPlayer", "simultaneousMobsAddedPerPlayer", "ticksBetweenSpawn", "spawnPotentials", "lootTablesToEject", "itemsToDropWhenOminous"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrialSpawnerConfig.class, "spawnRange;totalMobs;simultaneousMobs;totalMobsAddedPerPlayer;simultaneousMobsAddedPerPlayer;ticksBetweenSpawn;spawnPotentialsDefinition;lootTablesToEject;itemsToDropWhenOminous", "spawnRange", "totalMobs", "simultaneousMobs", "totalMobsAddedPerPlayer", "simultaneousMobsAddedPerPlayer", "ticksBetweenSpawn", "spawnPotentials", "lootTablesToEject", "itemsToDropWhenOminous"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrialSpawnerConfig.class, "spawnRange;totalMobs;simultaneousMobs;totalMobsAddedPerPlayer;simultaneousMobsAddedPerPlayer;ticksBetweenSpawn;spawnPotentialsDefinition;lootTablesToEject;itemsToDropWhenOminous", "spawnRange", "totalMobs", "simultaneousMobs", "totalMobsAddedPerPlayer", "simultaneousMobsAddedPerPlayer", "ticksBetweenSpawn", "spawnPotentials", "lootTablesToEject", "itemsToDropWhenOminous"}, this, object);
    }

    public int spawnRange() {
        return this.spawnRange;
    }

    public float totalMobs() {
        return this.totalMobs;
    }

    public float simultaneousMobs() {
        return this.simultaneousMobs;
    }

    public float totalMobsAddedPerPlayer() {
        return this.totalMobsAddedPerPlayer;
    }

    public float simultaneousMobsAddedPerPlayer() {
        return this.simultaneousMobsAddedPerPlayer;
    }

    public int ticksBetweenSpawn() {
        return this.ticksBetweenSpawn;
    }

    public Pool<MobSpawnerEntry> spawnPotentials() {
        return this.spawnPotentials;
    }

    public Pool<RegistryKey<LootTable>> lootTablesToEject() {
        return this.lootTablesToEject;
    }

    public RegistryKey<LootTable> itemsToDropWhenOminous() {
        return this.itemsToDropWhenOminous;
    }
}

