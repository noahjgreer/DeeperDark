package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;

public class DarkOakTrunkPlacer extends TrunkPlacer {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return fillTrunkPlacerFields(instance).apply(instance, DarkOakTrunkPlacer::new);
   });

   public DarkOakTrunkPlacer(int i, int j, int k) {
      super(i, j, k);
   }

   protected TrunkPlacerType getType() {
      return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
   }

   public List generate(TestableWorld world, BiConsumer replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
      List list = Lists.newArrayList();
      BlockPos blockPos = startPos.down();
      setToDirt(world, replacer, random, blockPos, config);
      setToDirt(world, replacer, random, blockPos.east(), config);
      setToDirt(world, replacer, random, blockPos.south(), config);
      setToDirt(world, replacer, random, blockPos.south().east(), config);
      Direction direction = Direction.Type.HORIZONTAL.random(random);
      int i = height - random.nextInt(4);
      int j = 2 - random.nextInt(3);
      int k = startPos.getX();
      int l = startPos.getY();
      int m = startPos.getZ();
      int n = k;
      int o = m;
      int p = l + height - 1;

      int q;
      int r;
      for(q = 0; q < height; ++q) {
         if (q >= i && j > 0) {
            n += direction.getOffsetX();
            o += direction.getOffsetZ();
            --j;
         }

         r = l + q;
         BlockPos blockPos2 = new BlockPos(n, r, o);
         if (TreeFeature.isAirOrLeaves(world, blockPos2)) {
            this.getAndSetState(world, replacer, random, blockPos2, config);
            this.getAndSetState(world, replacer, random, blockPos2.east(), config);
            this.getAndSetState(world, replacer, random, blockPos2.south(), config);
            this.getAndSetState(world, replacer, random, blockPos2.east().south(), config);
         }
      }

      list.add(new FoliagePlacer.TreeNode(new BlockPos(n, p, o), 0, true));

      for(q = -1; q <= 2; ++q) {
         for(r = -1; r <= 2; ++r) {
            if ((q < 0 || q > 1 || r < 0 || r > 1) && random.nextInt(3) <= 0) {
               int s = random.nextInt(3) + 2;

               for(int t = 0; t < s; ++t) {
                  this.getAndSetState(world, replacer, random, new BlockPos(k + q, p - t - 1, m + r), config);
               }

               list.add(new FoliagePlacer.TreeNode(new BlockPos(k + q, p, m + r), 0, false));
            }
         }
      }

      return list;
   }
}
