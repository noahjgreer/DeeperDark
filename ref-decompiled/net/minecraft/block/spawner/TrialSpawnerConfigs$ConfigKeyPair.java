/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerConfigs;
import net.minecraft.registry.RegistryKey;

static final class TrialSpawnerConfigs.ConfigKeyPair
extends Record {
    final RegistryKey<TrialSpawnerConfig> normal;
    final RegistryKey<TrialSpawnerConfig> ominous;

    private TrialSpawnerConfigs.ConfigKeyPair(RegistryKey<TrialSpawnerConfig> normal, RegistryKey<TrialSpawnerConfig> ominous) {
        this.normal = normal;
        this.ominous = ominous;
    }

    public static TrialSpawnerConfigs.ConfigKeyPair of(String id) {
        return new TrialSpawnerConfigs.ConfigKeyPair(TrialSpawnerConfigs.keyOf(id + "/normal"), TrialSpawnerConfigs.keyOf(id + "/ominous"));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrialSpawnerConfigs.ConfigKeyPair.class, "normal;ominous", "normal", "ominous"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrialSpawnerConfigs.ConfigKeyPair.class, "normal;ominous", "normal", "ominous"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrialSpawnerConfigs.ConfigKeyPair.class, "normal;ominous", "normal", "ominous"}, this, object);
    }

    public RegistryKey<TrialSpawnerConfig> normal() {
        return this.normal;
    }

    public RegistryKey<TrialSpawnerConfig> ominous() {
        return this.ominous;
    }
}
