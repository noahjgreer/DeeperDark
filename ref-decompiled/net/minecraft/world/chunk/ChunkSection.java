package net.minecraft.world.chunk;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class ChunkSection {
   public static final int field_31406 = 16;
   public static final int field_31407 = 16;
   public static final int field_31408 = 4096;
   public static final int field_34555 = 2;
   private short nonEmptyBlockCount;
   private short randomTickableBlockCount;
   private short nonEmptyFluidCount;
   private final PalettedContainer blockStateContainer;
   private ReadableContainer biomeContainer;

   private ChunkSection(ChunkSection section) {
      this.nonEmptyBlockCount = section.nonEmptyBlockCount;
      this.randomTickableBlockCount = section.randomTickableBlockCount;
      this.nonEmptyFluidCount = section.nonEmptyFluidCount;
      this.blockStateContainer = section.blockStateContainer.copy();
      this.biomeContainer = section.biomeContainer.copy();
   }

   public ChunkSection(PalettedContainer blockStateContainer, ReadableContainer biomeContainer) {
      this.blockStateContainer = blockStateContainer;
      this.biomeContainer = biomeContainer;
      this.calculateCounts();
   }

   public ChunkSection(Registry biomeRegistry) {
      this.blockStateContainer = new PalettedContainer(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
      this.biomeContainer = new PalettedContainer(biomeRegistry.getIndexedEntries(), biomeRegistry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
   }

   public BlockState getBlockState(int x, int y, int z) {
      return (BlockState)this.blockStateContainer.get(x, y, z);
   }

   public FluidState getFluidState(int x, int y, int z) {
      return ((BlockState)this.blockStateContainer.get(x, y, z)).getFluidState();
   }

   public void lock() {
      this.blockStateContainer.lock();
   }

   public void unlock() {
      this.blockStateContainer.unlock();
   }

   public BlockState setBlockState(int x, int y, int z, BlockState state) {
      return this.setBlockState(x, y, z, state, true);
   }

   public BlockState setBlockState(int x, int y, int z, BlockState state, boolean lock) {
      BlockState blockState;
      if (lock) {
         blockState = (BlockState)this.blockStateContainer.swap(x, y, z, state);
      } else {
         blockState = (BlockState)this.blockStateContainer.swapUnsafe(x, y, z, state);
      }

      FluidState fluidState = blockState.getFluidState();
      FluidState fluidState2 = state.getFluidState();
      if (!blockState.isAir()) {
         --this.nonEmptyBlockCount;
         if (blockState.hasRandomTicks()) {
            --this.randomTickableBlockCount;
         }
      }

      if (!fluidState.isEmpty()) {
         --this.nonEmptyFluidCount;
      }

      if (!state.isAir()) {
         ++this.nonEmptyBlockCount;
         if (state.hasRandomTicks()) {
            ++this.randomTickableBlockCount;
         }
      }

      if (!fluidState2.isEmpty()) {
         ++this.nonEmptyFluidCount;
      }

      return blockState;
   }

   public boolean isEmpty() {
      return this.nonEmptyBlockCount == 0;
   }

   public boolean hasRandomTicks() {
      return this.hasRandomBlockTicks() || this.hasRandomFluidTicks();
   }

   public boolean hasRandomBlockTicks() {
      return this.randomTickableBlockCount > 0;
   }

   public boolean hasRandomFluidTicks() {
      return this.nonEmptyFluidCount > 0;
   }

   public void calculateCounts() {
      class BlockStateCounter implements PalettedContainer.Counter {
         public int nonEmptyBlockCount;
         public int randomTickableBlockCount;
         public int nonEmptyFluidCount;

         BlockStateCounter(final ChunkSection chunkSection) {
         }

         public void accept(BlockState blockState, int i) {
            FluidState fluidState = blockState.getFluidState();
            if (!blockState.isAir()) {
               this.nonEmptyBlockCount += i;
               if (blockState.hasRandomTicks()) {
                  this.randomTickableBlockCount += i;
               }
            }

            if (!fluidState.isEmpty()) {
               this.nonEmptyBlockCount += i;
               if (fluidState.hasRandomTicks()) {
                  this.nonEmptyFluidCount += i;
               }
            }

         }

         // $FF: synthetic method
         public void accept(final Object object, final int i) {
            this.accept((BlockState)object, i);
         }
      }

      BlockStateCounter blockStateCounter = new BlockStateCounter(this);
      this.blockStateContainer.count(blockStateCounter);
      this.nonEmptyBlockCount = (short)blockStateCounter.nonEmptyBlockCount;
      this.randomTickableBlockCount = (short)blockStateCounter.randomTickableBlockCount;
      this.nonEmptyFluidCount = (short)blockStateCounter.nonEmptyFluidCount;
   }

   public PalettedContainer getBlockStateContainer() {
      return this.blockStateContainer;
   }

   public ReadableContainer getBiomeContainer() {
      return this.biomeContainer;
   }

   public void readDataPacket(PacketByteBuf buf) {
      this.nonEmptyBlockCount = buf.readShort();
      this.blockStateContainer.readPacket(buf);
      PalettedContainer palettedContainer = this.biomeContainer.slice();
      palettedContainer.readPacket(buf);
      this.biomeContainer = palettedContainer;
   }

   public void readBiomePacket(PacketByteBuf buf) {
      PalettedContainer palettedContainer = this.biomeContainer.slice();
      palettedContainer.readPacket(buf);
      this.biomeContainer = palettedContainer;
   }

   public void toPacket(PacketByteBuf buf) {
      buf.writeShort(this.nonEmptyBlockCount);
      this.blockStateContainer.writePacket(buf);
      this.biomeContainer.writePacket(buf);
   }

   public int getPacketSize() {
      return 2 + this.blockStateContainer.getPacketSize() + this.biomeContainer.getPacketSize();
   }

   public boolean hasAny(Predicate predicate) {
      return this.blockStateContainer.hasAny(predicate);
   }

   public RegistryEntry getBiome(int x, int y, int z) {
      return (RegistryEntry)this.biomeContainer.get(x, y, z);
   }

   public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler, int x, int y, int z) {
      PalettedContainer palettedContainer = this.biomeContainer.slice();
      int i = true;

      for(int j = 0; j < 4; ++j) {
         for(int k = 0; k < 4; ++k) {
            for(int l = 0; l < 4; ++l) {
               palettedContainer.swapUnsafe(j, k, l, biomeSupplier.getBiome(x + j, y + k, z + l, sampler));
            }
         }
      }

      this.biomeContainer = palettedContainer;
   }

   public ChunkSection copy() {
      return new ChunkSection(this);
   }
}
