package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public abstract class FallingBlock extends Block implements Falling {
   public FallingBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected abstract MapCodec getCodec();

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      world.scheduleBlockTick(pos, this, this.getFallDelay());
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      tickView.scheduleBlockTick(pos, this, this.getFallDelay());
      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
         FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
         this.configureFallingBlockEntity(fallingBlockEntity);
      }
   }

   protected void configureFallingBlockEntity(FallingBlockEntity entity) {
   }

   protected int getFallDelay() {
      return 2;
   }

   public static boolean canFallThrough(BlockState state) {
      return state.isAir() || state.isIn(BlockTags.FIRE) || state.isLiquid() || state.isReplaceable();
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextInt(16) == 0) {
         BlockPos blockPos = pos.down();
         if (canFallThrough(world.getBlockState(blockPos))) {
            ParticleUtil.spawnParticle(world, pos, (Random)random, (ParticleEffect)(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state)));
         }
      }

   }

   public abstract int getColor(BlockState state, BlockView world, BlockPos pos);
}
