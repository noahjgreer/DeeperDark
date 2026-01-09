package net.minecraft.world.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EmptyChunk extends WorldChunk {
   private final RegistryEntry biomeEntry;

   public EmptyChunk(World world, ChunkPos pos, RegistryEntry biomeEntry) {
      super(world, pos);
      this.biomeEntry = biomeEntry;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.VOID_AIR.getDefaultState();
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, int flags) {
      return null;
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.getDefaultState();
   }

   public int getLuminance(BlockPos pos) {
      return 0;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos, WorldChunk.CreationType creationType) {
      return null;
   }

   public void addBlockEntity(BlockEntity blockEntity) {
   }

   public void setBlockEntity(BlockEntity blockEntity) {
   }

   public void removeBlockEntity(BlockPos pos) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
      return true;
   }

   public ChunkLevelType getLevelType() {
      return ChunkLevelType.FULL;
   }

   public RegistryEntry getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      return this.biomeEntry;
   }
}
