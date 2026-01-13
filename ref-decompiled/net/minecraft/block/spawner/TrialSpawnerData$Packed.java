/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Uuids;

public static final class TrialSpawnerData.Packed
extends Record {
    final Set<UUID> detectedPlayers;
    final Set<UUID> currentMobs;
    final long cooldownEndsAt;
    final long nextMobSpawnsAt;
    final int totalMobsSpawned;
    final Optional<MobSpawnerEntry> nextSpawnData;
    final Optional<RegistryKey<LootTable>> ejectingLootTable;
    public static final MapCodec<TrialSpawnerData.Packed> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Uuids.SET_CODEC.lenientOptionalFieldOf("registered_players", Set.of()).forGetter(TrialSpawnerData.Packed::detectedPlayers), (App)Uuids.SET_CODEC.lenientOptionalFieldOf("current_mobs", Set.of()).forGetter(TrialSpawnerData.Packed::currentMobs), (App)Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", (Object)0L).forGetter(TrialSpawnerData.Packed::cooldownEndsAt), (App)Codec.LONG.lenientOptionalFieldOf(TrialSpawnerData.NEXT_MOB_SPAWNS_AT_KEY, (Object)0L).forGetter(TrialSpawnerData.Packed::nextMobSpawnsAt), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", (Object)0).forGetter(TrialSpawnerData.Packed::totalMobsSpawned), (App)MobSpawnerEntry.CODEC.lenientOptionalFieldOf(TrialSpawnerData.SPAWN_DATA_KEY).forGetter(TrialSpawnerData.Packed::nextSpawnData), (App)LootTable.TABLE_KEY.lenientOptionalFieldOf("ejecting_loot_table").forGetter(TrialSpawnerData.Packed::ejectingLootTable)).apply((Applicative)instance, TrialSpawnerData.Packed::new));

    public TrialSpawnerData.Packed(Set<UUID> detectedPlayers, Set<UUID> currentMobs, long cooldownEndsAt, long nextMobSpawnsAt, int totalMobsSpawned, Optional<MobSpawnerEntry> nextSpawnData, Optional<RegistryKey<LootTable>> ejectingLootTable) {
        this.detectedPlayers = detectedPlayers;
        this.currentMobs = currentMobs;
        this.cooldownEndsAt = cooldownEndsAt;
        this.nextMobSpawnsAt = nextMobSpawnsAt;
        this.totalMobsSpawned = totalMobsSpawned;
        this.nextSpawnData = nextSpawnData;
        this.ejectingLootTable = ejectingLootTable;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrialSpawnerData.Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrialSpawnerData.Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrialSpawnerData.Packed.class, "detectedPlayers;currentMobs;cooldownEndsAt;nextMobSpawnsAt;totalMobsSpawned;nextSpawnData;ejectingLootTable", "detectedPlayers", "currentMobs", "cooldownEndsAt", "nextMobSpawnsAt", "totalMobsSpawned", "nextSpawnData", "ejectingLootTable"}, this, object);
    }

    public Set<UUID> detectedPlayers() {
        return this.detectedPlayers;
    }

    public Set<UUID> currentMobs() {
        return this.currentMobs;
    }

    public long cooldownEndsAt() {
        return this.cooldownEndsAt;
    }

    public long nextMobSpawnsAt() {
        return this.nextMobSpawnsAt;
    }

    public int totalMobsSpawned() {
        return this.totalMobsSpawned;
    }

    public Optional<MobSpawnerEntry> nextSpawnData() {
        return this.nextSpawnData;
    }

    public Optional<RegistryKey<LootTable>> ejectingLootTable() {
        return this.ejectingLootTable;
    }
}
