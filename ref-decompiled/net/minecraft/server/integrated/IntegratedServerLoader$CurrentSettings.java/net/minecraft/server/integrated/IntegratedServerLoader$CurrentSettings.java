/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.server.integrated;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;

@Environment(value=EnvType.CLIENT)
static final class IntegratedServerLoader.CurrentSettings
extends Record {
    final LevelInfo levelInfo;
    final GeneratorOptions options;
    final Registry<DimensionOptions> existingDimensionRegistry;

    IntegratedServerLoader.CurrentSettings(LevelInfo levelInfo, GeneratorOptions options, Registry<DimensionOptions> existingDimensionRegistry) {
        this.levelInfo = levelInfo;
        this.options = options;
        this.existingDimensionRegistry = existingDimensionRegistry;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IntegratedServerLoader.CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IntegratedServerLoader.CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IntegratedServerLoader.CurrentSettings.class, "levelSettings;options;existingDimensions", "levelInfo", "options", "existingDimensionRegistry"}, this, object);
    }

    public LevelInfo levelInfo() {
        return this.levelInfo;
    }

    public GeneratorOptions options() {
        return this.options;
    }

    public Registry<DimensionOptions> existingDimensionRegistry() {
        return this.existingDimensionRegistry;
    }
}
