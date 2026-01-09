package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SculkPatchFeature extends Feature {
   public SculkPatchFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();
      if (!this.canGenerate(structureWorldAccess, blockPos)) {
         return false;
      } else {
         SculkPatchFeatureConfig sculkPatchFeatureConfig = (SculkPatchFeatureConfig)context.getConfig();
         Random random = context.getRandom();
         SculkSpreadManager sculkSpreadManager = SculkSpreadManager.createWorldGen();
         int i = sculkPatchFeatureConfig.spreadRounds() + sculkPatchFeatureConfig.growthRounds();

         int k;
         int l;
         for(int j = 0; j < i; ++j) {
            for(k = 0; k < sculkPatchFeatureConfig.chargeCount(); ++k) {
               sculkSpreadManager.spread(blockPos, sculkPatchFeatureConfig.amountPerCharge());
            }

            boolean bl = j < sculkPatchFeatureConfig.spreadRounds();

            for(l = 0; l < sculkPatchFeatureConfig.spreadAttempts(); ++l) {
               sculkSpreadManager.tick(structureWorldAccess, blockPos, random, bl);
            }

            sculkSpreadManager.clearCursors();
         }

         BlockPos blockPos2 = blockPos.down();
         if (random.nextFloat() <= sculkPatchFeatureConfig.catalystChance() && structureWorldAccess.getBlockState(blockPos2).isFullCube(structureWorldAccess, blockPos2)) {
            structureWorldAccess.setBlockState(blockPos, Blocks.SCULK_CATALYST.getDefaultState(), 3);
         }

         k = sculkPatchFeatureConfig.extraRareGrowths().get(random);

         for(l = 0; l < k; ++l) {
            BlockPos blockPos3 = blockPos.add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            if (structureWorldAccess.getBlockState(blockPos3).isAir() && structureWorldAccess.getBlockState(blockPos3.down()).isSideSolidFullSquare(structureWorldAccess, blockPos3.down(), Direction.UP)) {
               structureWorldAccess.setBlockState(blockPos3, (BlockState)Blocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, true), 3);
            }
         }

         return true;
      }
   }

   private boolean canGenerate(WorldAccess world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      if (blockState.getBlock() instanceof SculkSpreadable) {
         return true;
      } else if (!blockState.isAir() && (!blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill())) {
         return false;
      } else {
         Stream var10000 = Direction.stream();
         Objects.requireNonNull(pos);
         return var10000.map(pos::offset).anyMatch((pos2) -> {
            return world.getBlockState(pos2).isFullCube(world, pos2);
         });
      }
   }
}
