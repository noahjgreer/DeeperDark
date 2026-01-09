package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DiskFeature extends Feature {
   public DiskFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      DiskFeatureConfig diskFeatureConfig = (DiskFeatureConfig)context.getConfig();
      BlockPos blockPos = context.getOrigin();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      Random random = context.getRandom();
      boolean bl = false;
      int i = blockPos.getY();
      int j = i + diskFeatureConfig.halfHeight();
      int k = i - diskFeatureConfig.halfHeight() - 1;
      int l = diskFeatureConfig.radius().get(random);
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Iterator var12 = BlockPos.iterate(blockPos.add(-l, 0, -l), blockPos.add(l, 0, l)).iterator();

      while(var12.hasNext()) {
         BlockPos blockPos2 = (BlockPos)var12.next();
         int m = blockPos2.getX() - blockPos.getX();
         int n = blockPos2.getZ() - blockPos.getZ();
         if (m * m + n * n <= l * l) {
            bl |= this.placeBlock(diskFeatureConfig, structureWorldAccess, random, j, k, mutable.set(blockPos2));
         }
      }

      return bl;
   }

   protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, Random random, int topY, int bottomY, BlockPos.Mutable pos) {
      boolean bl = false;
      boolean bl2 = false;

      for(int i = topY; i > bottomY; --i) {
         pos.setY(i);
         if (config.target().test(world, pos)) {
            BlockState blockState = config.stateProvider().getBlockState(world, random, pos);
            world.setBlockState(pos, blockState, 2);
            if (!bl2) {
               this.markBlocksAboveForPostProcessing(world, pos);
            }

            bl = true;
            bl2 = true;
         } else {
            bl2 = false;
         }
      }

      return bl;
   }
}
