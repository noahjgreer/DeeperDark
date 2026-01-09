package net.minecraft.block;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public interface Waterloggable extends FluidDrainable, FluidFillable {
   default boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
      return fluid == Fluids.WATER;
   }

   default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
         if (!world.isClient()) {
            world.setBlockState(pos, (BlockState)state.with(Properties.WATERLOGGED, true), 3);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
         }

         return true;
      } else {
         return false;
      }
   }

   default ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
      if ((Boolean)state.get(Properties.WATERLOGGED)) {
         world.setBlockState(pos, (BlockState)state.with(Properties.WATERLOGGED, false), 3);
         if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
         }

         return new ItemStack(Items.WATER_BUCKET);
      } else {
         return ItemStack.EMPTY;
      }
   }

   default Optional getBucketFillSound() {
      return Fluids.WATER.getBucketFillSound();
   }
}
