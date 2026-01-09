package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class Carver {
   public static final Carver CAVE;
   public static final Carver NETHER_CAVE;
   public static final Carver RAVINE;
   protected static final BlockState AIR;
   protected static final BlockState CAVE_AIR;
   protected static final FluidState WATER;
   protected static final FluidState LAVA;
   protected Set carvableFluids;
   private final MapCodec codec;

   private static Carver register(String name, Carver carver) {
      return (Carver)Registry.register(Registries.CARVER, (String)name, carver);
   }

   public Carver(Codec configCodec) {
      this.carvableFluids = ImmutableSet.of(Fluids.WATER);
      this.codec = configCodec.fieldOf("config").xmap(this::configure, ConfiguredCarver::config);
   }

   public ConfiguredCarver configure(CarverConfig config) {
      return new ConfiguredCarver(this, config);
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   public int getBranchFactor() {
      return 4;
   }

   protected boolean carveRegion(CarverContext context, CarverConfig config, Chunk chunk, Function posToBiome, AquiferSampler aquiferSampler, double x, double y, double z, double width, double height, CarvingMask mask, SkipPredicate skipPredicate) {
      ChunkPos chunkPos = chunk.getPos();
      double d = (double)chunkPos.getCenterX();
      double e = (double)chunkPos.getCenterZ();
      double f = 16.0 + width * 2.0;
      if (!(Math.abs(x - d) > f) && !(Math.abs(z - e) > f)) {
         int i = chunkPos.getStartX();
         int j = chunkPos.getStartZ();
         int k = Math.max(MathHelper.floor(x - width) - i - 1, 0);
         int l = Math.min(MathHelper.floor(x + width) - i, 15);
         int m = Math.max(MathHelper.floor(y - height) - 1, context.getMinY() + 1);
         int n = chunk.hasBelowZeroRetrogen() ? 0 : 7;
         int o = Math.min(MathHelper.floor(y + height) + 1, context.getMinY() + context.getHeight() - 1 - n);
         int p = Math.max(MathHelper.floor(z - width) - j - 1, 0);
         int q = Math.min(MathHelper.floor(z + width) - j, 15);
         boolean bl = false;
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         BlockPos.Mutable mutable2 = new BlockPos.Mutable();

         for(int r = k; r <= l; ++r) {
            int s = chunkPos.getOffsetX(r);
            double g = ((double)s + 0.5 - x) / width;

            for(int t = p; t <= q; ++t) {
               int u = chunkPos.getOffsetZ(t);
               double h = ((double)u + 0.5 - z) / width;
               if (!(g * g + h * h >= 1.0)) {
                  MutableBoolean mutableBoolean = new MutableBoolean(false);

                  for(int v = o; v > m; --v) {
                     double w = ((double)v - 0.5 - y) / height;
                     if (!skipPredicate.shouldSkip(context, g, w, h, v) && (!mask.get(r, v, t) || isDebug(config))) {
                        mask.set(r, v, t);
                        mutable.set(s, v, u);
                        bl |= this.carveAtPoint(context, config, chunk, posToBiome, mask, mutable, mutable2, aquiferSampler, mutableBoolean);
                     }
                  }
               }
            }
         }

         return bl;
      } else {
         return false;
      }
   }

   protected boolean carveAtPoint(CarverContext context, CarverConfig config, Chunk chunk, Function posToBiome, CarvingMask mask, BlockPos.Mutable pos, BlockPos.Mutable tmp, AquiferSampler aquiferSampler, MutableBoolean replacedGrassy) {
      BlockState blockState = chunk.getBlockState(pos);
      if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
         replacedGrassy.setTrue();
      }

      if (!this.canAlwaysCarveBlock(config, blockState) && !isDebug(config)) {
         return false;
      } else {
         BlockState blockState2 = this.getState(context, config, pos, aquiferSampler);
         if (blockState2 == null) {
            return false;
         } else {
            chunk.setBlockState(pos, blockState2);
            if (aquiferSampler.needsFluidTick() && !blockState2.getFluidState().isEmpty()) {
               chunk.markBlockForPostProcessing(pos);
            }

            if (replacedGrassy.isTrue()) {
               tmp.set(pos, (Direction)Direction.DOWN);
               if (chunk.getBlockState(tmp).isOf(Blocks.DIRT)) {
                  context.applyMaterialRule(posToBiome, chunk, tmp, !blockState2.getFluidState().isEmpty()).ifPresent((state) -> {
                     chunk.setBlockState(tmp, state);
                     if (!state.getFluidState().isEmpty()) {
                        chunk.markBlockForPostProcessing(tmp);
                     }

                  });
               }
            }

            return true;
         }
      }
   }

   @Nullable
   private BlockState getState(CarverContext context, CarverConfig config, BlockPos pos, AquiferSampler sampler) {
      if (pos.getY() <= config.lavaLevel.getY(context)) {
         return LAVA.getBlockState();
      } else {
         BlockState blockState = sampler.apply(new DensityFunction.UnblendedNoisePos(pos.getX(), pos.getY(), pos.getZ()), 0.0);
         if (blockState == null) {
            return isDebug(config) ? config.debugConfig.getBarrierState() : null;
         } else {
            return isDebug(config) ? getDebugState(config, blockState) : blockState;
         }
      }
   }

   private static BlockState getDebugState(CarverConfig config, BlockState state) {
      if (state.isOf(Blocks.AIR)) {
         return config.debugConfig.getAirState();
      } else if (state.isOf(Blocks.WATER)) {
         BlockState blockState = config.debugConfig.getWaterState();
         return blockState.contains(Properties.WATERLOGGED) ? (BlockState)blockState.with(Properties.WATERLOGGED, true) : blockState;
      } else {
         return state.isOf(Blocks.LAVA) ? config.debugConfig.getLavaState() : state;
      }
   }

   public abstract boolean carve(CarverContext context, CarverConfig config, Chunk chunk, Function posToBiome, Random random, AquiferSampler aquiferSampler, ChunkPos pos, CarvingMask mask);

   public abstract boolean shouldCarve(CarverConfig config, Random random);

   protected boolean canAlwaysCarveBlock(CarverConfig config, BlockState state) {
      return state.isIn(config.replaceable);
   }

   protected static boolean canCarveBranch(ChunkPos pos, double x, double z, int branchIndex, int branchCount, float baseWidth) {
      double d = (double)pos.getCenterX();
      double e = (double)pos.getCenterZ();
      double f = x - d;
      double g = z - e;
      double h = (double)(branchCount - branchIndex);
      double i = (double)(baseWidth + 2.0F + 16.0F);
      return f * f + g * g - h * h <= i * i;
   }

   private static boolean isDebug(CarverConfig config) {
      return config.debugConfig.isDebugMode();
   }

   static {
      CAVE = register("cave", new CaveCarver(CaveCarverConfig.CAVE_CODEC));
      NETHER_CAVE = register("nether_cave", new NetherCaveCarver(CaveCarverConfig.CAVE_CODEC));
      RAVINE = register("canyon", new RavineCarver(RavineCarverConfig.RAVINE_CODEC));
      AIR = Blocks.AIR.getDefaultState();
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
      WATER = Fluids.WATER.getDefaultState();
      LAVA = Fluids.LAVA.getDefaultState();
   }

   public interface SkipPredicate {
      boolean shouldSkip(CarverContext context, double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y);
   }
}
