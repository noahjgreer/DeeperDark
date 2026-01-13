/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.world.WorldCreationSettings
 *  net.minecraft.resource.DataConfiguration
 *  net.minecraft.world.level.WorldGenSettings
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.level.WorldGenSettings;

@Environment(value=EnvType.CLIENT)
public record WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
    private final WorldGenSettings worldGenSettings;
    private final DataConfiguration dataConfiguration;

    public WorldCreationSettings(WorldGenSettings worldGenSettings, DataConfiguration dataConfiguration) {
        this.worldGenSettings = worldGenSettings;
        this.dataConfiguration = dataConfiguration;
    }

    public WorldGenSettings worldGenSettings() {
        return this.worldGenSettings;
    }

    public DataConfiguration dataConfiguration() {
        return this.dataConfiguration;
    }
}

