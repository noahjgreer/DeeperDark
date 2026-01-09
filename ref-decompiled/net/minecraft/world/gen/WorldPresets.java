package net.minecraft.world.gen;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class WorldPresets {
   public static final RegistryKey DEFAULT = of("normal");
   public static final RegistryKey FLAT = of("flat");
   public static final RegistryKey LARGE_BIOMES = of("large_biomes");
   public static final RegistryKey AMPLIFIED = of("amplified");
   public static final RegistryKey SINGLE_BIOME_SURFACE = of("single_biome_surface");
   public static final RegistryKey DEBUG_ALL_BLOCK_STATES = of("debug_all_block_states");

   public static void bootstrap(Registerable presetRegisterable) {
      (new Registrar(presetRegisterable)).bootstrap();
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.WORLD_PRESET, Identifier.ofVanilla(id));
   }

   public static Optional getWorldPreset(DimensionOptionsRegistryHolder registry) {
      return registry.getOrEmpty(DimensionOptions.OVERWORLD).flatMap((overworld) -> {
         ChunkGenerator var10000 = overworld.chunkGenerator();
         Objects.requireNonNull(var10000);
         ChunkGenerator chunkGenerator = var10000;
         int i = 0;
         Optional var6;
         switch (chunkGenerator.typeSwitch<invokedynamic>(chunkGenerator, i)) {
            case 0:
               FlatChunkGenerator flatChunkGenerator = (FlatChunkGenerator)chunkGenerator;
               var6 = Optional.of(FLAT);
               break;
            case 1:
               DebugChunkGenerator debugChunkGenerator = (DebugChunkGenerator)chunkGenerator;
               var6 = Optional.of(DEBUG_ALL_BLOCK_STATES);
               break;
            case 2:
               NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)chunkGenerator;
               var6 = Optional.of(DEFAULT);
               break;
            default:
               var6 = Optional.empty();
         }

         return var6;
      });
   }

   public static DimensionOptionsRegistryHolder createDemoOptions(RegistryWrapper.WrapperLookup registries) {
      return ((WorldPreset)registries.getOrThrow(RegistryKeys.WORLD_PRESET).getOrThrow(DEFAULT).value()).createDimensionsRegistryHolder();
   }

   public static DimensionOptions getDefaultOverworldOptions(RegistryWrapper.WrapperLookup registries) {
      return (DimensionOptions)((WorldPreset)registries.getOrThrow(RegistryKeys.WORLD_PRESET).getOrThrow(DEFAULT).value()).getOverworld().orElseThrow();
   }

   public static DimensionOptionsRegistryHolder createTestOptions(RegistryWrapper.WrapperLookup registries) {
      return ((WorldPreset)registries.getOrThrow(RegistryKeys.WORLD_PRESET).getOrThrow(FLAT).value()).createDimensionsRegistryHolder();
   }

   private static class Registrar {
      private final Registerable presetRegisterable;
      private final RegistryEntryLookup chunkGeneratorSettingsLookup;
      private final RegistryEntryLookup biomeLookup;
      private final RegistryEntryLookup featureLookup;
      private final RegistryEntryLookup structureSetLookup;
      private final RegistryEntryLookup multiNoisePresetLookup;
      private final RegistryEntry overworldDimensionType;
      private final DimensionOptions netherDimensionOptions;
      private final DimensionOptions endDimensionOptions;

      Registrar(Registerable presetRegisterable) {
         this.presetRegisterable = presetRegisterable;
         RegistryEntryLookup registryEntryLookup = presetRegisterable.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
         this.chunkGeneratorSettingsLookup = presetRegisterable.getRegistryLookup(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
         this.biomeLookup = presetRegisterable.getRegistryLookup(RegistryKeys.BIOME);
         this.featureLookup = presetRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
         this.structureSetLookup = presetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE_SET);
         this.multiNoisePresetLookup = presetRegisterable.getRegistryLookup(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         this.overworldDimensionType = registryEntryLookup.getOrThrow(DimensionTypes.OVERWORLD);
         RegistryEntry registryEntry = registryEntryLookup.getOrThrow(DimensionTypes.THE_NETHER);
         RegistryEntry registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.NETHER);
         RegistryEntry.Reference reference = this.multiNoisePresetLookup.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);
         this.netherDimensionOptions = new DimensionOptions(registryEntry, new NoiseChunkGenerator(MultiNoiseBiomeSource.create((RegistryEntry)reference), registryEntry2));
         RegistryEntry registryEntry3 = registryEntryLookup.getOrThrow(DimensionTypes.THE_END);
         RegistryEntry registryEntry4 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.END);
         this.endDimensionOptions = new DimensionOptions(registryEntry3, new NoiseChunkGenerator(TheEndBiomeSource.createVanilla(this.biomeLookup), registryEntry4));
      }

      private DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator) {
         return new DimensionOptions(this.overworldDimensionType, chunkGenerator);
      }

      private DimensionOptions createOverworldOptions(BiomeSource biomeSource, RegistryEntry chunkGeneratorSettings) {
         return this.createOverworldOptions(new NoiseChunkGenerator(biomeSource, chunkGeneratorSettings));
      }

      private WorldPreset createPreset(DimensionOptions dimensionOptions) {
         return new WorldPreset(Map.of(DimensionOptions.OVERWORLD, dimensionOptions, DimensionOptions.NETHER, this.netherDimensionOptions, DimensionOptions.END, this.endDimensionOptions));
      }

      private void register(RegistryKey key, DimensionOptions dimensionOptions) {
         this.presetRegisterable.register(key, this.createPreset(dimensionOptions));
      }

      private void bootstrap(BiomeSource biomeSource) {
         RegistryEntry registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
         this.register(WorldPresets.DEFAULT, this.createOverworldOptions(biomeSource, registryEntry));
         RegistryEntry registryEntry2 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.LARGE_BIOMES);
         this.register(WorldPresets.LARGE_BIOMES, this.createOverworldOptions(biomeSource, registryEntry2));
         RegistryEntry registryEntry3 = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.AMPLIFIED);
         this.register(WorldPresets.AMPLIFIED, this.createOverworldOptions(biomeSource, registryEntry3));
      }

      public void bootstrap() {
         RegistryEntry.Reference reference = this.multiNoisePresetLookup.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
         this.bootstrap(MultiNoiseBiomeSource.create((RegistryEntry)reference));
         RegistryEntry registryEntry = this.chunkGeneratorSettingsLookup.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
         RegistryEntry.Reference reference2 = this.biomeLookup.getOrThrow(BiomeKeys.PLAINS);
         this.register(WorldPresets.SINGLE_BIOME_SURFACE, this.createOverworldOptions(new FixedBiomeSource(reference2), registryEntry));
         this.register(WorldPresets.FLAT, this.createOverworldOptions(new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig(this.biomeLookup, this.structureSetLookup, this.featureLookup))));
         this.register(WorldPresets.DEBUG_ALL_BLOCK_STATES, this.createOverworldOptions(new DebugChunkGenerator(reference2)));
      }
   }
}
