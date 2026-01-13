/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.world.InitialWorldOptions
 *  net.minecraft.client.gui.screen.world.WorldCreator$Mode
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.world.gen.FlatLevelGeneratorPreset
 *  net.minecraft.world.rule.ServerGameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.rule.ServerGameRules;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record InitialWorldOptions(WorldCreator.Mode selectedGameMode, ServerGameRules gameRuleOverwrites, @Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset) {
    private final WorldCreator.Mode selectedGameMode;
    private final ServerGameRules gameRuleOverwrites;
    private final @Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset;

    public InitialWorldOptions(WorldCreator.Mode selectedGameMode, ServerGameRules gameRuleOverwrites, @Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset) {
        this.selectedGameMode = selectedGameMode;
        this.gameRuleOverwrites = gameRuleOverwrites;
        this.flatLevelPreset = flatLevelPreset;
    }

    public WorldCreator.Mode selectedGameMode() {
        return this.selectedGameMode;
    }

    public ServerGameRules gameRuleOverwrites() {
        return this.gameRuleOverwrites;
    }

    public @Nullable RegistryKey<FlatLevelGeneratorPreset> flatLevelPreset() {
        return this.flatLevelPreset;
    }
}

