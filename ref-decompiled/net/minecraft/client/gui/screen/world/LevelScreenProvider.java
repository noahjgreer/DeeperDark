/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen
 *  net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen
 *  net.minecraft.client.gui.screen.world.LevelScreenProvider
 *  net.minecraft.client.world.GeneratorOptionsHolder
 *  net.minecraft.client.world.GeneratorOptionsHolder$RegistryAwareModifier
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryEntryLookup
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.source.BiomeSource
 *  net.minecraft.world.biome.source.FixedBiomeSource
 *  net.minecraft.world.gen.WorldPreset
 *  net.minecraft.world.gen.WorldPresets
 *  net.minecraft.world.gen.chunk.ChunkGenerator
 *  net.minecraft.world.gen.chunk.ChunkGeneratorSettings
 *  net.minecraft.world.gen.chunk.FlatChunkGenerator
 *  net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
 *  net.minecraft.world.gen.chunk.NoiseChunkGenerator
 */
package net.minecraft.client.gui.screen.world;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface LevelScreenProvider {
    public static final Map<Optional<RegistryKey<WorldPreset>>, LevelScreenProvider> WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(Optional.of(WorldPresets.FLAT), (parent, generatorOptionsHolder) -> {
        ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
        Registry registryEntryLookup = dynamicRegistryManager.getOrThrow(RegistryKeys.BIOME);
        Registry registryEntryLookup2 = dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE_SET);
        Registry registryEntryLookup3 = dynamicRegistryManager.getOrThrow(RegistryKeys.PLACED_FEATURE);
        return new CustomizeFlatLevelScreen(parent, config -> parent.getWorldCreator().applyModifier(LevelScreenProvider.createModifier((FlatChunkGeneratorConfig)config)), chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig((RegistryEntryLookup)registryEntryLookup, (RegistryEntryLookup)registryEntryLookup2, (RegistryEntryLookup)registryEntryLookup3));
    }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (parent, generatorOptionsHolder) -> new CustomizeBuffetLevelScreen((Screen)parent, generatorOptionsHolder, biomeEntry -> parent.getWorldCreator().applyModifier(LevelScreenProvider.createModifier((RegistryEntry)biomeEntry))));

    public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptionsHolder var2);

    public static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            FlatChunkGenerator chunkGenerator = new FlatChunkGenerator(config);
            return dimensionsRegistryHolder.with((RegistryWrapper.WrapperLookup)dynamicRegistryManager, (ChunkGenerator)chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            Registry registry = dynamicRegistryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            RegistryEntry.Reference registryEntry2 = registry.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
            FixedBiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
            NoiseChunkGenerator chunkGenerator = new NoiseChunkGenerator((BiomeSource)biomeSource, (RegistryEntry)registryEntry2);
            return dimensionsRegistryHolder.with((RegistryWrapper.WrapperLookup)dynamicRegistryManager, (ChunkGenerator)chunkGenerator);
        };
    }
}

