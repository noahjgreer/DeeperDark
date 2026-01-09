package net.minecraft.world.gen.feature.util;

import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FeatureContext {
   private final Optional feature;
   private final StructureWorldAccess world;
   private final ChunkGenerator generator;
   private final Random random;
   private final BlockPos origin;
   private final FeatureConfig config;

   public FeatureContext(Optional feature, StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos origin, FeatureConfig config) {
      this.feature = feature;
      this.world = world;
      this.generator = generator;
      this.random = random;
      this.origin = origin;
      this.config = config;
   }

   public Optional getFeature() {
      return this.feature;
   }

   public StructureWorldAccess getWorld() {
      return this.world;
   }

   public ChunkGenerator getGenerator() {
      return this.generator;
   }

   public Random getRandom() {
      return this.random;
   }

   public BlockPos getOrigin() {
      return this.origin;
   }

   public FeatureConfig getConfig() {
      return this.config;
   }
}
