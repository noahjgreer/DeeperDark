package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class MegaJungleTrunkPlacer extends GiantTrunkPlacer {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return fillTrunkPlacerFields(instance).apply(instance, MegaJungleTrunkPlacer::new);
   });

   public MegaJungleTrunkPlacer(int i, int j, int k) {
      super(i, j, k);
   }

   protected TrunkPlacerType getType() {
      return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
   }

   public List generate(TestableWorld world, BiConsumer replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
      List list = Lists.newArrayList();
      list.addAll(super.generate(world, replacer, random, height, startPos, config));

      for(int i = height - 2 - random.nextInt(4); i > height / 2; i -= 2 + random.nextInt(4)) {
         float f = random.nextFloat() * 6.2831855F;
         int j = 0;
         int k = 0;

         for(int l = 0; l < 5; ++l) {
            j = (int)(1.5F + MathHelper.cos(f) * (float)l);
            k = (int)(1.5F + MathHelper.sin(f) * (float)l);
            BlockPos blockPos = startPos.add(j, i - 3 + l / 2, k);
            this.getAndSetState(world, replacer, random, blockPos, config);
         }

         list.add(new FoliagePlacer.TreeNode(startPos.add(j, i, k), -2, false));
      }

      return list;
   }
}
