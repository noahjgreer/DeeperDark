package net.minecraft.world.gen.placementmodifier;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public abstract class AbstractCountPlacementModifier extends PlacementModifier {
   protected abstract int getCount(Random random, BlockPos pos);

   public Stream getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
      return IntStream.range(0, this.getCount(random, pos)).mapToObj((i) -> {
         return pos;
      });
   }
}
