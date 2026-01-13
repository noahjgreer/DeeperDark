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
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public static final class TrialSpawnerLogic.FullConfig
extends Record {
    final RegistryEntry<TrialSpawnerConfig> normal;
    final RegistryEntry<TrialSpawnerConfig> ominous;
    final int targetCooldownLength;
    final int requiredPlayerRange;
    public static final MapCodec<TrialSpawnerLogic.FullConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TrialSpawnerConfig.ENTRY_CODEC.optionalFieldOf("normal_config", RegistryEntry.of(TrialSpawnerConfig.DEFAULT)).forGetter(TrialSpawnerLogic.FullConfig::normal), (App)TrialSpawnerConfig.ENTRY_CODEC.optionalFieldOf("ominous_config", RegistryEntry.of(TrialSpawnerConfig.DEFAULT)).forGetter(TrialSpawnerLogic.FullConfig::ominous), (App)Codecs.NON_NEGATIVE_INT.optionalFieldOf("target_cooldown_length", (Object)36000).forGetter(TrialSpawnerLogic.FullConfig::targetCooldownLength), (App)Codec.intRange((int)1, (int)128).optionalFieldOf("required_player_range", (Object)14).forGetter(TrialSpawnerLogic.FullConfig::requiredPlayerRange)).apply((Applicative)instance, TrialSpawnerLogic.FullConfig::new));
    public static final TrialSpawnerLogic.FullConfig DEFAULT = new TrialSpawnerLogic.FullConfig(RegistryEntry.of(TrialSpawnerConfig.DEFAULT), RegistryEntry.of(TrialSpawnerConfig.DEFAULT), 36000, 14);

    public TrialSpawnerLogic.FullConfig(RegistryEntry<TrialSpawnerConfig> normal, RegistryEntry<TrialSpawnerConfig> ominous, int targetCooldownLength, int requiredPlayerRange) {
        this.normal = normal;
        this.ominous = ominous;
        this.targetCooldownLength = targetCooldownLength;
        this.requiredPlayerRange = requiredPlayerRange;
    }

    public TrialSpawnerLogic.FullConfig withEntityType(EntityType<?> entityType) {
        return new TrialSpawnerLogic.FullConfig(RegistryEntry.of(this.normal.value().withSpawnPotential(entityType)), RegistryEntry.of(this.ominous.value().withSpawnPotential(entityType)), this.targetCooldownLength, this.requiredPlayerRange);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrialSpawnerLogic.FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrialSpawnerLogic.FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrialSpawnerLogic.FullConfig.class, "normal;ominous;targetCooldownLength;requiredPlayerRange", "normal", "ominous", "targetCooldownLength", "requiredPlayerRange"}, this, object);
    }

    public RegistryEntry<TrialSpawnerConfig> normal() {
        return this.normal;
    }

    public RegistryEntry<TrialSpawnerConfig> ominous() {
        return this.ominous;
    }

    public int targetCooldownLength() {
        return this.targetCooldownLength;
    }

    public int requiredPlayerRange() {
        return this.requiredPlayerRange;
    }
}
