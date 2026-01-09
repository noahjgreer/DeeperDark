package net.minecraft.client.gui.screen.world;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Environment(EnvType.CLIENT)
public interface LevelScreenProvider {
   Map WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(Optional.of(WorldPresets.FLAT), (parent, generatorOptionsHolder) -> {
      ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
      DynamicRegistryManager dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
      RegistryEntryLookup registryEntryLookup = dynamicRegistryManager.getOrThrow(RegistryKeys.BIOME);
      RegistryEntryLookup registryEntryLookup2 = dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE_SET);
      RegistryEntryLookup registryEntryLookup3 = dynamicRegistryManager.getOrThrow(RegistryKeys.PLACED_FEATURE);
      return new CustomizeFlatLevelScreen(parent, (config) -> {
         parent.getWorldCreator().applyModifier(createModifier(config));
      }, chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(registryEntryLookup, registryEntryLookup2, registryEntryLookup3));
   }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (parent, generatorOptionsHolder) -> {
      return new CustomizeBuffetLevelScreen(parent, generatorOptionsHolder, (biomeEntry) -> {
         parent.getWorldCreator().applyModifier(createModifier(biomeEntry));
      });
   });

   Screen createEditScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder);

   static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
      return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
         ChunkGenerator chunkGenerator = new FlatChunkGenerator(config);
         return dimensionsRegistryHolder.with(dynamicRegistryManager, chunkGenerator);
      };
   }

   private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry biomeEntry) {
      return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
         Registry registry = dynamicRegistryManager.getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
         RegistryEntry registryEntry2 = registry.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
         BiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
         ChunkGenerator chunkGenerator = new NoiseChunkGenerator(biomeSource, registryEntry2);
         return dimensionsRegistryHolder.with(dynamicRegistryManager, chunkGenerator);
      };
   }
}
