package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public enum EmptyBlockRenderView implements BlockRenderView {
   INSTANCE;

   public float getBrightness(Direction direction, boolean shaded) {
      return 1.0F;
   }

   public LightingProvider getLightingProvider() {
      return LightingProvider.DEFAULT;
   }

   public int getColor(BlockPos pos, ColorResolver colorResolver) {
      return -1;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.getDefaultState();
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.getDefaultState();
   }

   public int getHeight() {
      return 0;
   }

   public int getBottomY() {
      return 0;
   }

   // $FF: synthetic method
   private static EmptyBlockRenderView[] method_61721() {
      return new EmptyBlockRenderView[]{INSTANCE};
   }
}
