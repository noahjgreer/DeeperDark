package net.minecraft.fluid;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class WaterFluid extends FlowableFluid {
   public Fluid getFlowing() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getStill() {
      return Fluids.WATER;
   }

   public Item getBucketItem() {
      return Items.WATER_BUCKET;
   }

   public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
      if (!state.isStill() && !(Boolean)state.get(FALLING)) {
         if (random.nextInt(64) == 0) {
            world.playSoundClient((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.AMBIENT, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         world.addParticleClient(ParticleTypes.UNDERWATER, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + random.nextDouble(), (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
      }

   }

   @Nullable
   public ParticleEffect getParticle() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean isInfinite(ServerWorld world) {
      return world.getGameRules().getBoolean(GameRules.WATER_SOURCE_CONVERSION);
   }

   protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
      BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
      Block.dropStacks(state, world, pos, blockEntity);
   }

   protected void onEntityCollision(World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      handler.addEvent(CollisionEvent.EXTINGUISH);
   }

   public int getMaxFlowDistance(WorldView world) {
      return 4;
   }

   public BlockState toBlockState(FluidState state) {
      return (BlockState)Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
   }

   public boolean matchesType(Fluid fluid) {
      return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
   }

   public int getLevelDecreasePerBlock(WorldView world) {
      return 1;
   }

   public int getTickRate(WorldView world) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
      return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
   }

   protected float getBlastResistance() {
      return 100.0F;
   }

   public Optional getBucketFillSound() {
      return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
   }

   public static class Flowing extends WaterFluid {
      protected void appendProperties(StateManager.Builder builder) {
         super.appendProperties(builder);
         builder.add(LEVEL);
      }

      public int getLevel(FluidState state) {
         return (Integer)state.get(LEVEL);
      }

      public boolean isStill(FluidState state) {
         return false;
      }
   }

   public static class Still extends WaterFluid {
      public int getLevel(FluidState state) {
         return 8;
      }

      public boolean isStill(FluidState state) {
         return true;
      }
   }
}
