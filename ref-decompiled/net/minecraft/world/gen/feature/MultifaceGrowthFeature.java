package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class MultifaceGrowthFeature extends Feature {
   public MultifaceGrowthFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();
      Random random = context.getRandom();
      MultifaceGrowthFeatureConfig multifaceGrowthFeatureConfig = (MultifaceGrowthFeatureConfig)context.getConfig();
      if (!isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
         return false;
      } else {
         List list = multifaceGrowthFeatureConfig.shuffleDirections(random);
         if (generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), multifaceGrowthFeatureConfig, random, list)) {
            return true;
         } else {
            BlockPos.Mutable mutable = blockPos.mutableCopy();
            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
               Direction direction = (Direction)var8.next();
               mutable.set(blockPos);
               List list2 = multifaceGrowthFeatureConfig.shuffleDirections(random, direction.getOpposite());

               for(int i = 0; i < multifaceGrowthFeatureConfig.searchRange; ++i) {
                  mutable.set(blockPos, (Direction)direction);
                  BlockState blockState = structureWorldAccess.getBlockState(mutable);
                  if (!isAirOrWater(blockState) && !blockState.isOf(multifaceGrowthFeatureConfig.block)) {
                     break;
                  }

                  if (generate(structureWorldAccess, mutable, blockState, multifaceGrowthFeatureConfig, random, list2)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean generate(StructureWorldAccess world, BlockPos pos, BlockState state, MultifaceGrowthFeatureConfig config, Random random, List directions) {
      BlockPos.Mutable mutable = pos.mutableCopy();
      Iterator var7 = directions.iterator();

      Direction direction;
      BlockState blockState;
      do {
         if (!var7.hasNext()) {
            return false;
         }

         direction = (Direction)var7.next();
         blockState = world.getBlockState(mutable.set(pos, (Direction)direction));
      } while(!blockState.isIn(config.canPlaceOn));

      BlockState blockState2 = config.block.withDirection(state, world, pos, direction);
      if (blockState2 == null) {
         return false;
      } else {
         world.setBlockState(pos, blockState2, 3);
         world.getChunk(pos).markBlockForPostProcessing(pos);
         if (random.nextFloat() < config.spreadChance) {
            config.block.getGrower().grow(blockState2, world, pos, direction, (Random)random, true);
         }

         return true;
      }
   }

   private static boolean isAirOrWater(BlockState state) {
      return state.isAir() || state.isOf(Blocks.WATER);
   }
}
