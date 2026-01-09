package net.minecraft.world.chunk.light;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkProvider;

public final class ChunkBlockLightProvider extends ChunkLightProvider {
   private final BlockPos.Mutable mutablePos;

   public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
      this(chunkProvider, new BlockLightStorage(chunkProvider));
   }

   @VisibleForTesting
   public ChunkBlockLightProvider(ChunkProvider chunkProvider, BlockLightStorage blockLightStorage) {
      super(chunkProvider, blockLightStorage);
      this.mutablePos = new BlockPos.Mutable();
   }

   protected void method_51529(long blockPos) {
      long l = ChunkSectionPos.fromBlockPos(blockPos);
      if (((BlockLightStorage)this.lightStorage).hasSection(l)) {
         BlockState blockState = this.getStateForLighting(this.mutablePos.set(blockPos));
         int i = this.getLightSourceLuminance(blockPos, blockState);
         int j = ((BlockLightStorage)this.lightStorage).get(blockPos);
         if (i < j) {
            ((BlockLightStorage)this.lightStorage).set(blockPos, 0);
            this.method_51565(blockPos, ChunkLightProvider.class_8531.packWithAllDirectionsSet(j));
         } else {
            this.method_51565(blockPos, field_44731);
         }

         if (i > 0) {
            this.method_51566(blockPos, ChunkLightProvider.class_8531.method_51573(i, isTrivialForLighting(blockState)));
         }

      }
   }

   protected void method_51531(long blockPos, long packed, int lightLevel) {
      BlockState blockState = null;
      Direction[] var7 = DIRECTIONS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Direction direction = var7[var9];
         if (ChunkLightProvider.class_8531.isDirectionBitSet(packed, direction)) {
            long l = BlockPos.offset(blockPos, direction);
            if (((BlockLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(l))) {
               int i = ((BlockLightStorage)this.lightStorage).get(l);
               int j = lightLevel - 1;
               if (j > i) {
                  this.mutablePos.set(l);
                  BlockState blockState2 = this.getStateForLighting(this.mutablePos);
                  int k = lightLevel - this.getOpacity(blockState2);
                  if (k > i) {
                     if (blockState == null) {
                        blockState = ChunkLightProvider.class_8531.isTrivial(packed) ? Blocks.AIR.getDefaultState() : this.getStateForLighting(this.mutablePos.set(blockPos));
                     }

                     if (!this.shapesCoverFullCube(blockState, blockState2, direction)) {
                        ((BlockLightStorage)this.lightStorage).set(l, k);
                        if (k > 1) {
                           this.method_51566(l, ChunkLightProvider.class_8531.packWithOneDirectionCleared(k, isTrivialForLighting(blockState2), direction.getOpposite()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   protected void method_51530(long blockPos, long packed) {
      int i = ChunkLightProvider.class_8531.getLightLevel(packed);
      Direction[] var6 = DIRECTIONS;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (ChunkLightProvider.class_8531.isDirectionBitSet(packed, direction)) {
            long l = BlockPos.offset(blockPos, direction);
            if (((BlockLightStorage)this.lightStorage).hasSection(ChunkSectionPos.fromBlockPos(l))) {
               int j = ((BlockLightStorage)this.lightStorage).get(l);
               if (j != 0) {
                  if (j <= i - 1) {
                     BlockState blockState = this.getStateForLighting(this.mutablePos.set(l));
                     int k = this.getLightSourceLuminance(l, blockState);
                     ((BlockLightStorage)this.lightStorage).set(l, 0);
                     if (k < j) {
                        this.method_51565(l, ChunkLightProvider.class_8531.packWithOneDirectionCleared(j, direction.getOpposite()));
                     }

                     if (k > 0) {
                        this.method_51566(l, ChunkLightProvider.class_8531.method_51573(k, isTrivialForLighting(blockState)));
                     }
                  } else {
                     this.method_51566(l, ChunkLightProvider.class_8531.method_51579(j, false, direction.getOpposite()));
                  }
               }
            }
         }
      }

   }

   private int getLightSourceLuminance(long blockPos, BlockState blockState) {
      int i = blockState.getLuminance();
      return i > 0 && ((BlockLightStorage)this.lightStorage).isSectionInEnabledColumn(ChunkSectionPos.fromBlockPos(blockPos)) ? i : 0;
   }

   public void propagateLight(ChunkPos chunkPos) {
      this.setColumnEnabled(chunkPos, true);
      LightSourceView lightSourceView = this.chunkProvider.getChunk(chunkPos.x, chunkPos.z);
      if (lightSourceView != null) {
         lightSourceView.forEachLightSource((blockPos, blockState) -> {
            int i = blockState.getLuminance();
            this.method_51566(blockPos.asLong(), ChunkLightProvider.class_8531.method_51573(i, isTrivialForLighting(blockState)));
         });
      }

   }
}
