package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

/** @deprecated */
@Deprecated
public class LakeFeature extends Feature {
   private static final BlockState CAVE_AIR;

   public LakeFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      BlockPos blockPos = context.getOrigin();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      Random random = context.getRandom();
      Config config = (Config)context.getConfig();
      if (blockPos.getY() <= structureWorldAccess.getBottomY() + 4) {
         return false;
      } else {
         blockPos = blockPos.down(4);
         boolean[] bls = new boolean[2048];
         int i = random.nextInt(4) + 4;

         for(int j = 0; j < i; ++j) {
            double d = random.nextDouble() * 6.0 + 3.0;
            double e = random.nextDouble() * 4.0 + 2.0;
            double f = random.nextDouble() * 6.0 + 3.0;
            double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
            double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
            double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;

            for(int l = 1; l < 15; ++l) {
               for(int m = 1; m < 15; ++m) {
                  for(int n = 1; n < 7; ++n) {
                     double o = ((double)l - g) / (d / 2.0);
                     double p = ((double)n - h) / (e / 2.0);
                     double q = ((double)m - k) / (f / 2.0);
                     double r = o * o + p * p + q * q;
                     if (r < 1.0) {
                        bls[(l * 16 + m) * 8 + n] = true;
                     }
                  }
               }
            }
         }

         BlockState blockState = config.fluid().get(random, blockPos);

         int t;
         boolean v;
         int s;
         int u;
         for(s = 0; s < 16; ++s) {
            for(t = 0; t < 16; ++t) {
               for(u = 0; u < 8; ++u) {
                  v = !bls[(s * 16 + t) * 8 + u] && (s < 15 && bls[((s + 1) * 16 + t) * 8 + u] || s > 0 && bls[((s - 1) * 16 + t) * 8 + u] || t < 15 && bls[(s * 16 + t + 1) * 8 + u] || t > 0 && bls[(s * 16 + (t - 1)) * 8 + u] || u < 7 && bls[(s * 16 + t) * 8 + u + 1] || u > 0 && bls[(s * 16 + t) * 8 + (u - 1)]);
                  if (v) {
                     BlockState blockState2 = structureWorldAccess.getBlockState(blockPos.add(s, u, t));
                     if (u >= 4 && blockState2.isLiquid()) {
                        return false;
                     }

                     if (u < 4 && !blockState2.isSolid() && structureWorldAccess.getBlockState(blockPos.add(s, u, t)) != blockState) {
                        return false;
                     }
                  }
               }
            }
         }

         boolean bl2;
         for(s = 0; s < 16; ++s) {
            for(t = 0; t < 16; ++t) {
               for(u = 0; u < 8; ++u) {
                  if (bls[(s * 16 + t) * 8 + u]) {
                     BlockPos blockPos2 = blockPos.add(s, u, t);
                     if (this.canReplace(structureWorldAccess.getBlockState(blockPos2))) {
                        bl2 = u >= 4;
                        structureWorldAccess.setBlockState(blockPos2, bl2 ? CAVE_AIR : blockState, 2);
                        if (bl2) {
                           structureWorldAccess.scheduleBlockTick(blockPos2, CAVE_AIR.getBlock(), 0);
                           this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos2);
                        }
                     }
                  }
               }
            }
         }

         BlockState blockState3 = config.barrier().get(random, blockPos);
         if (!blockState3.isAir()) {
            for(t = 0; t < 16; ++t) {
               for(u = 0; u < 16; ++u) {
                  for(int v = 0; v < 8; ++v) {
                     bl2 = !bls[(t * 16 + u) * 8 + v] && (t < 15 && bls[((t + 1) * 16 + u) * 8 + v] || t > 0 && bls[((t - 1) * 16 + u) * 8 + v] || u < 15 && bls[(t * 16 + u + 1) * 8 + v] || u > 0 && bls[(t * 16 + (u - 1)) * 8 + v] || v < 7 && bls[(t * 16 + u) * 8 + v + 1] || v > 0 && bls[(t * 16 + u) * 8 + (v - 1)]);
                     if (bl2 && (v < 4 || random.nextInt(2) != 0)) {
                        BlockState blockState4 = structureWorldAccess.getBlockState(blockPos.add(t, v, u));
                        if (blockState4.isSolid() && !blockState4.isIn(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                           BlockPos blockPos3 = blockPos.add(t, v, u);
                           structureWorldAccess.setBlockState(blockPos3, blockState3, 2);
                           this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos3);
                        }
                     }
                  }
               }
            }
         }

         if (blockState.getFluidState().isIn(FluidTags.WATER)) {
            for(t = 0; t < 16; ++t) {
               for(u = 0; u < 16; ++u) {
                  v = true;
                  BlockPos blockPos4 = blockPos.add(t, 4, u);
                  if (((Biome)structureWorldAccess.getBiome(blockPos4).value()).canSetIce(structureWorldAccess, blockPos4, false) && this.canReplace(structureWorldAccess.getBlockState(blockPos4))) {
                     structureWorldAccess.setBlockState(blockPos4, Blocks.ICE.getDefaultState(), 2);
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean canReplace(BlockState state) {
      return !state.isIn(BlockTags.FEATURES_CANNOT_REPLACE);
   }

   static {
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
   }

   public static record Config(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfig {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("fluid").forGetter(Config::fluid), BlockStateProvider.TYPE_CODEC.fieldOf("barrier").forGetter(Config::barrier)).apply(instance, Config::new);
      });

      public Config(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2) {
         this.fluid = blockStateProvider;
         this.barrier = blockStateProvider2;
      }

      public BlockStateProvider fluid() {
         return this.fluid;
      }

      public BlockStateProvider barrier() {
         return this.barrier;
      }
   }
}
