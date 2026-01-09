package net.minecraft.util.math;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.server.world.ServerWorld;

public record BlockPointer(ServerWorld world, BlockPos pos, BlockState state, DispenserBlockEntity blockEntity) {
   public BlockPointer(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, DispenserBlockEntity dispenserBlockEntity) {
      this.world = serverWorld;
      this.pos = blockPos;
      this.state = blockState;
      this.blockEntity = dispenserBlockEntity;
   }

   public Vec3d centerPos() {
      return this.pos.toCenterPos();
   }

   public ServerWorld world() {
      return this.world;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public BlockState state() {
      return this.state;
   }

   public DispenserBlockEntity blockEntity() {
      return this.blockEntity;
   }
}
