package net.minecraft.structure;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

@FunctionalInterface
public interface StructurePiecesGenerator {
   void generatePieces(StructurePiecesCollector collector, Context context);

   public static record Context(FeatureConfig config, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ChunkPos chunkPos, HeightLimitView world, ChunkRandom random, long seed) {
      public Context(FeatureConfig featureConfig, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ChunkPos chunkPos, HeightLimitView heightLimitView, ChunkRandom chunkRandom, long l) {
         this.config = featureConfig;
         this.chunkGenerator = chunkGenerator;
         this.structureTemplateManager = structureTemplateManager;
         this.chunkPos = chunkPos;
         this.world = heightLimitView;
         this.random = chunkRandom;
         this.seed = l;
      }

      public FeatureConfig config() {
         return this.config;
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public StructureTemplateManager structureTemplateManager() {
         return this.structureTemplateManager;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public HeightLimitView world() {
         return this.world;
      }

      public ChunkRandom random() {
         return this.random;
      }

      public long seed() {
         return this.seed;
      }
   }
}
