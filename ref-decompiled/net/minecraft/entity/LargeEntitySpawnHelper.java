package net.minecraft.entity;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class LargeEntitySpawnHelper {
   public static Optional trySpawnAt(EntityType entityType, SpawnReason reason, ServerWorld world, BlockPos pos, int tries, int horizontalRange, int verticalRange, Requirements requirements, boolean requireEmptySpace) {
      BlockPos.Mutable mutable = pos.mutableCopy();

      for(int i = 0; i < tries; ++i) {
         int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
         int k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
         mutable.set((Vec3i)pos, j, verticalRange, k);
         if (world.getWorldBorder().contains((BlockPos)mutable) && findSpawnPos(world, verticalRange, mutable, requirements) && (!requireEmptySpace || world.isSpaceEmpty(entityType.getSpawnBox((double)mutable.getX() + 0.5, (double)mutable.getY(), (double)mutable.getZ() + 0.5)))) {
            MobEntity mobEntity = (MobEntity)entityType.create(world, (Consumer)null, mutable, reason, false, false);
            if (mobEntity != null) {
               if (mobEntity.canSpawn(world, reason) && mobEntity.canSpawn(world)) {
                  world.spawnEntityAndPassengers(mobEntity);
                  mobEntity.playAmbientSound();
                  return Optional.of(mobEntity);
               }

               mobEntity.discard();
            }
         }
      }

      return Optional.empty();
   }

   private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos, Requirements requirements) {
      BlockPos.Mutable mutable = (new BlockPos.Mutable()).set(pos);
      BlockState blockState = world.getBlockState(mutable);

      for(int i = verticalRange; i >= -verticalRange; --i) {
         pos.move(Direction.DOWN);
         mutable.set(pos, (Direction)Direction.UP);
         BlockState blockState2 = world.getBlockState(pos);
         if (requirements.canSpawnOn(world, pos, blockState2, mutable, blockState)) {
            pos.move(Direction.UP);
            return true;
         }

         blockState = blockState2;
      }

      return false;
   }

   public interface Requirements {
      /** @deprecated */
      @Deprecated
      Requirements IRON_GOLEM = (world, pos, state, abovePos, aboveState) -> {
         if (!state.isOf(Blocks.COBWEB) && !state.isOf(Blocks.CACTUS) && !state.isOf(Blocks.GLASS_PANE) && !(state.getBlock() instanceof StainedGlassPaneBlock) && !(state.getBlock() instanceof StainedGlassBlock) && !(state.getBlock() instanceof LeavesBlock) && !state.isOf(Blocks.CONDUIT) && !state.isOf(Blocks.ICE) && !state.isOf(Blocks.TNT) && !state.isOf(Blocks.GLOWSTONE) && !state.isOf(Blocks.BEACON) && !state.isOf(Blocks.SEA_LANTERN) && !state.isOf(Blocks.FROSTED_ICE) && !state.isOf(Blocks.TINTED_GLASS) && !state.isOf(Blocks.GLASS)) {
            return (aboveState.isAir() || aboveState.isLiquid()) && (state.isSolid() || state.isOf(Blocks.POWDER_SNOW));
         } else {
            return false;
         }
      };
      Requirements WARDEN = (world, pos, state, abovePos, aboveState) -> {
         return aboveState.getCollisionShape(world, abovePos).isEmpty() && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);
      };
      Requirements CREAKING = (world, pos, state, abovePos, aboveState) -> {
         return aboveState.getCollisionShape(world, abovePos).isEmpty() && !state.isIn(BlockTags.LEAVES) && Block.isFaceFullSquare(state.getCollisionShape(world, pos), Direction.UP);
      };

      boolean canSpawnOn(ServerWorld world, BlockPos pos, BlockState state, BlockPos abovePos, BlockState aboveState);
   }
}
