package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.jetbrains.annotations.Nullable;

public record ChainedBlockSource(ChunkNoiseSampler.BlockStateSampler[] samplers) implements ChunkNoiseSampler.BlockStateSampler {
   public ChainedBlockSource(ChunkNoiseSampler.BlockStateSampler[] blockStateSamplers) {
      this.samplers = blockStateSamplers;
   }

   @Nullable
   public BlockState sample(DensityFunction.NoisePos pos) {
      ChunkNoiseSampler.BlockStateSampler[] var2 = this.samplers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkNoiseSampler.BlockStateSampler blockStateSampler = var2[var4];
         BlockState blockState = blockStateSampler.sample(pos);
         if (blockState != null) {
            return blockState;
         }
      }

      return null;
   }

   public ChunkNoiseSampler.BlockStateSampler[] samplers() {
      return this.samplers;
   }
}
