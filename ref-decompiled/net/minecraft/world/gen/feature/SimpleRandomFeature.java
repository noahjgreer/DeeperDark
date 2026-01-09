package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleRandomFeature extends Feature {
   public SimpleRandomFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      Random random = context.getRandom();
      SimpleRandomFeatureConfig simpleRandomFeatureConfig = (SimpleRandomFeatureConfig)context.getConfig();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();
      ChunkGenerator chunkGenerator = context.getGenerator();
      int i = random.nextInt(simpleRandomFeatureConfig.features.size());
      PlacedFeature placedFeature = (PlacedFeature)simpleRandomFeatureConfig.features.get(i).value();
      return placedFeature.generateUnregistered(structureWorldAccess, chunkGenerator, random, blockPos);
   }
}
