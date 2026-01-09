package net.minecraft.structure;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.noise.NoiseConfig;

@FunctionalInterface
public interface StructureGeneratorFactory {
   Optional createGenerator(Context context);

   static StructureGeneratorFactory simple(Predicate predicate, StructurePiecesGenerator generator) {
      Optional optional = Optional.of(generator);
      return (context) -> {
         return predicate.test(context) ? optional : Optional.empty();
      };
   }

   static Predicate checkForBiomeOnTop(Heightmap.Type heightmapType) {
      return (context) -> {
         return context.isBiomeValid(heightmapType);
      };
   }

   public static record Context(ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, long seed, ChunkPos chunkPos, FeatureConfig config, HeightLimitView world, Predicate validBiome, StructureTemplateManager structureTemplateManager, DynamicRegistryManager registryManager) {
      public Context(ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, long l, ChunkPos chunkPos, FeatureConfig featureConfig, HeightLimitView heightLimitView, Predicate predicate, StructureTemplateManager structureTemplateManager, DynamicRegistryManager dynamicRegistryManager) {
         this.chunkGenerator = chunkGenerator;
         this.biomeSource = biomeSource;
         this.noiseConfig = noiseConfig;
         this.seed = l;
         this.chunkPos = chunkPos;
         this.config = featureConfig;
         this.world = heightLimitView;
         this.validBiome = predicate;
         this.structureTemplateManager = structureTemplateManager;
         this.registryManager = dynamicRegistryManager;
      }

      public boolean isBiomeValid(Heightmap.Type heightmapType) {
         int i = this.chunkPos.getCenterX();
         int j = this.chunkPos.getCenterZ();
         int k = this.chunkGenerator.getHeightInGround(i, j, heightmapType, this.world, this.noiseConfig);
         RegistryEntry registryEntry = this.chunkGenerator.getBiomeSource().getBiome(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(k), BiomeCoords.fromBlock(j), this.noiseConfig.getMultiNoiseSampler());
         return this.validBiome.test(registryEntry);
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public BiomeSource biomeSource() {
         return this.biomeSource;
      }

      public NoiseConfig noiseConfig() {
         return this.noiseConfig;
      }

      public long seed() {
         return this.seed;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public FeatureConfig config() {
         return this.config;
      }

      public HeightLimitView world() {
         return this.world;
      }

      public Predicate validBiome() {
         return this.validBiome;
      }

      public StructureTemplateManager structureTemplateManager() {
         return this.structureTemplateManager;
      }

      public DynamicRegistryManager registryManager() {
         return this.registryManager;
      }
   }
}
