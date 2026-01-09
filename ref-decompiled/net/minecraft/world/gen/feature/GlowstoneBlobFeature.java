package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GlowstoneBlobFeature extends Feature {
   public GlowstoneBlobFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();
      Random random = context.getRandom();
      if (!structureWorldAccess.isAir(blockPos)) {
         return false;
      } else {
         BlockState blockState = structureWorldAccess.getBlockState(blockPos.up());
         if (!blockState.isOf(Blocks.NETHERRACK) && !blockState.isOf(Blocks.BASALT) && !blockState.isOf(Blocks.BLACKSTONE)) {
            return false;
         } else {
            structureWorldAccess.setBlockState(blockPos, Blocks.GLOWSTONE.getDefaultState(), 2);

            for(int i = 0; i < 1500; ++i) {
               BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), -random.nextInt(12), random.nextInt(8) - random.nextInt(8));
               if (structureWorldAccess.getBlockState(blockPos2).isAir()) {
                  int j = 0;
                  Direction[] var9 = Direction.values();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Direction direction = var9[var11];
                     if (structureWorldAccess.getBlockState(blockPos2.offset(direction)).isOf(Blocks.GLOWSTONE)) {
                        ++j;
                     }

                     if (j > 1) {
                        break;
                     }
                  }

                  if (j == 1) {
                     structureWorldAccess.setBlockState(blockPos2, Blocks.GLOWSTONE.getDefaultState(), 2);
                  }
               }
            }

            return true;
         }
      }
   }
}
