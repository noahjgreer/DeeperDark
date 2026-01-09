package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RandomFeature extends Feature {
   public RandomFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      RandomFeatureConfig randomFeatureConfig = (RandomFeatureConfig)context.getConfig();
      Random random = context.getRandom();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      ChunkGenerator chunkGenerator = context.getGenerator();
      BlockPos blockPos = context.getOrigin();
      Iterator var7 = randomFeatureConfig.features.iterator();

      RandomFeatureEntry randomFeatureEntry;
      do {
         if (!var7.hasNext()) {
            return ((PlacedFeature)randomFeatureConfig.defaultFeature.value()).generateUnregistered(structureWorldAccess, chunkGenerator, random, blockPos);
         }

         randomFeatureEntry = (RandomFeatureEntry)var7.next();
      } while(!(random.nextFloat() < randomFeatureEntry.chance));

      return randomFeatureEntry.generate(structureWorldAccess, chunkGenerator, random, blockPos);
   }
}
