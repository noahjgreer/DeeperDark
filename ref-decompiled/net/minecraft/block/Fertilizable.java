package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public interface Fertilizable {
   boolean isFertilizable(WorldView world, BlockPos pos, BlockState state);

   boolean canGrow(World world, Random random, BlockPos pos, BlockState state);

   void grow(ServerWorld world, Random random, BlockPos pos, BlockState state);

   static boolean canSpread(WorldView world, BlockPos pos, BlockState state) {
      return findPosToSpreadTo(Direction.Type.HORIZONTAL.stream().toList(), world, pos, state).isPresent();
   }

   static Optional findPosToSpreadTo(World world, BlockPos pos, BlockState state) {
      return findPosToSpreadTo(Direction.Type.HORIZONTAL.getShuffled(world.random), world, pos, state);
   }

   private static Optional findPosToSpreadTo(List directions, WorldView world, BlockPos pos, BlockState state) {
      Iterator var4 = directions.iterator();

      BlockPos blockPos;
      do {
         if (!var4.hasNext()) {
            return Optional.empty();
         }

         Direction direction = (Direction)var4.next();
         blockPos = pos.offset(direction);
      } while(!world.isAir(blockPos) || !state.canPlaceAt(world, blockPos));

      return Optional.of(blockPos);
   }

   default BlockPos getFertilizeParticlePos(BlockPos pos) {
      BlockPos var10000;
      switch (this.getFertilizableType().ordinal()) {
         case 0:
            var10000 = pos.up();
            break;
         case 1:
            var10000 = pos;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   default FertilizableType getFertilizableType() {
      return Fertilizable.FertilizableType.GROWER;
   }

   public static enum FertilizableType {
      NEIGHBOR_SPREADER,
      GROWER;

      // $FF: synthetic method
      private static FertilizableType[] method_55771() {
         return new FertilizableType[]{NEIGHBOR_SPREADER, GROWER};
      }
   }
}
