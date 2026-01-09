package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ScatteredOreFeature extends Feature {
   private static final int MAX_SPREAD = 7;

   ScatteredOreFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      Random random = context.getRandom();
      OreFeatureConfig oreFeatureConfig = (OreFeatureConfig)context.getConfig();
      BlockPos blockPos = context.getOrigin();
      int i = random.nextInt(oreFeatureConfig.size + 1);
      BlockPos.Mutable mutable = new BlockPos.Mutable();

      for(int j = 0; j < i; ++j) {
         this.setPos(mutable, random, blockPos, Math.min(j, 7));
         BlockState blockState = structureWorldAccess.getBlockState(mutable);
         Iterator var10 = oreFeatureConfig.targets.iterator();

         while(var10.hasNext()) {
            OreFeatureConfig.Target target = (OreFeatureConfig.Target)var10.next();
            Objects.requireNonNull(structureWorldAccess);
            if (OreFeature.shouldPlace(blockState, structureWorldAccess::getBlockState, random, oreFeatureConfig, target, mutable)) {
               structureWorldAccess.setBlockState(mutable, target.state, 2);
               break;
            }
         }
      }

      return true;
   }

   private void setPos(BlockPos.Mutable mutable, Random random, BlockPos origin, int spread) {
      int i = this.getSpread(random, spread);
      int j = this.getSpread(random, spread);
      int k = this.getSpread(random, spread);
      mutable.set((Vec3i)origin, i, j, k);
   }

   private int getSpread(Random random, int spread) {
      return Math.round((random.nextFloat() - random.nextFloat()) * (float)spread);
   }
}
