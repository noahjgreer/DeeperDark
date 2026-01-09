package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomBooleanFeature extends Feature {
   public RandomBooleanFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      Random random = context.getRandom();
      RandomBooleanFeatureConfig randomBooleanFeatureConfig = (RandomBooleanFeatureConfig)context.getConfig();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      ChunkGenerator chunkGenerator = context.getGenerator();
      BlockPos blockPos = context.getOrigin();
      boolean bl = random.nextBoolean();
      return ((PlacedFeature)(bl ? randomBooleanFeatureConfig.featureTrue : randomBooleanFeatureConfig.featureFalse).value()).generateUnregistered(structureWorldAccess, chunkGenerator, random, blockPos);
   }
}
