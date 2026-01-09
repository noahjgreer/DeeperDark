package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.OreVeinSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.jetbrains.annotations.Nullable;

public class ChunkNoiseSampler implements DensityFunction.EachApplier, DensityFunction.NoisePos {
   private final GenerationShapeConfig generationShapeConfig;
   final int horizontalCellCount;
   final int verticalCellCount;
   final int minimumCellY;
   private final int startCellX;
   private final int startCellZ;
   final int startBiomeX;
   final int startBiomeZ;
   final List interpolators;
   final List caches;
   private final Map actualDensityFunctionCache = new HashMap();
   private final Long2IntMap surfaceHeightEstimateCache = new Long2IntOpenHashMap();
   private final AquiferSampler aquiferSampler;
   private final DensityFunction initialDensityWithoutJaggedness;
   private final BlockStateSampler blockStateSampler;
   private final Blender blender;
   private final FlatCache cachedBlendAlphaDensityFunction;
   private final FlatCache cachedBlendOffsetDensityFunction;
   private final DensityFunctionTypes.Beardifying beardifying;
   private long lastBlendingColumnPos;
   private Blender.BlendResult lastBlendingResult;
   final int horizontalBiomeEnd;
   final int horizontalCellBlockCount;
   final int verticalCellBlockCount;
   boolean isInInterpolationLoop;
   boolean isSamplingForCaches;
   private int startBlockX;
   int startBlockY;
   private int startBlockZ;
   int cellBlockX;
   int cellBlockY;
   int cellBlockZ;
   long sampleUniqueIndex;
   long cacheOnceUniqueIndex;
   int index;
   private final DensityFunction.EachApplier interpolationEachApplier;

   public static ChunkNoiseSampler create(Chunk chunk, NoiseConfig noiseConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
      GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig().trimHeight(chunk);
      ChunkPos chunkPos = chunk.getPos();
      int i = 16 / generationShapeConfig.horizontalCellBlockCount();
      return new ChunkNoiseSampler(i, noiseConfig, chunkPos.getStartX(), chunkPos.getStartZ(), generationShapeConfig, beardifying, chunkGeneratorSettings, fluidLevelSampler, blender);
   }

   public ChunkNoiseSampler(int horizontalCellCount, NoiseConfig noiseConfig, int startBlockX, int startBlockZ, GenerationShapeConfig generationShapeConfig, DensityFunctionTypes.Beardifying beardifying, ChunkGeneratorSettings chunkGeneratorSettings, AquiferSampler.FluidLevelSampler fluidLevelSampler, Blender blender) {
      this.lastBlendingColumnPos = ChunkPos.MARKER;
      this.lastBlendingResult = new Blender.BlendResult(1.0, 0.0);
      this.interpolationEachApplier = new DensityFunction.EachApplier() {
         public DensityFunction.NoisePos at(int index) {
            ChunkNoiseSampler.this.startBlockY = (index + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalCellBlockCount;
            ++ChunkNoiseSampler.this.sampleUniqueIndex;
            ChunkNoiseSampler.this.cellBlockY = 0;
            ChunkNoiseSampler.this.index = index;
            return ChunkNoiseSampler.this;
         }

         public void fill(double[] densities, DensityFunction densityFunction) {
            for(int i = 0; i < ChunkNoiseSampler.this.verticalCellCount + 1; ++i) {
               ChunkNoiseSampler.this.startBlockY = (i + ChunkNoiseSampler.this.minimumCellY) * ChunkNoiseSampler.this.verticalCellBlockCount;
               ++ChunkNoiseSampler.this.sampleUniqueIndex;
               ChunkNoiseSampler.this.cellBlockY = 0;
               ChunkNoiseSampler.this.index = i;
               densities[i] = densityFunction.sample(ChunkNoiseSampler.this);
            }

         }
      };
      this.generationShapeConfig = generationShapeConfig;
      this.horizontalCellBlockCount = generationShapeConfig.horizontalCellBlockCount();
      this.verticalCellBlockCount = generationShapeConfig.verticalCellBlockCount();
      this.horizontalCellCount = horizontalCellCount;
      this.verticalCellCount = MathHelper.floorDiv(generationShapeConfig.height(), this.verticalCellBlockCount);
      this.minimumCellY = MathHelper.floorDiv(generationShapeConfig.minimumY(), this.verticalCellBlockCount);
      this.startCellX = Math.floorDiv(startBlockX, this.horizontalCellBlockCount);
      this.startCellZ = Math.floorDiv(startBlockZ, this.horizontalCellBlockCount);
      this.interpolators = Lists.newArrayList();
      this.caches = Lists.newArrayList();
      this.startBiomeX = BiomeCoords.fromBlock(startBlockX);
      this.startBiomeZ = BiomeCoords.fromBlock(startBlockZ);
      this.horizontalBiomeEnd = BiomeCoords.fromBlock(horizontalCellCount * this.horizontalCellBlockCount);
      this.blender = blender;
      this.beardifying = beardifying;
      this.cachedBlendAlphaDensityFunction = new FlatCache(new BlendAlphaDensityFunction(), false);
      this.cachedBlendOffsetDensityFunction = new FlatCache(new BlendOffsetDensityFunction(), false);

      int k;
      int l;
      for(int i = 0; i <= this.horizontalBiomeEnd; ++i) {
         int j = this.startBiomeX + i;
         k = BiomeCoords.toBlock(j);

         for(l = 0; l <= this.horizontalBiomeEnd; ++l) {
            int m = this.startBiomeZ + l;
            int n = BiomeCoords.toBlock(m);
            Blender.BlendResult blendResult = blender.calculate(k, n);
            this.cachedBlendAlphaDensityFunction.cache[i][l] = blendResult.alpha();
            this.cachedBlendOffsetDensityFunction.cache[i][l] = blendResult.blendingOffset();
         }
      }

      NoiseRouter noiseRouter = noiseConfig.getNoiseRouter();
      NoiseRouter noiseRouter2 = noiseRouter.apply(this::getActualDensityFunction);
      if (!chunkGeneratorSettings.hasAquifers()) {
         this.aquiferSampler = AquiferSampler.seaLevel(fluidLevelSampler);
      } else {
         k = ChunkSectionPos.getSectionCoord(startBlockX);
         l = ChunkSectionPos.getSectionCoord(startBlockZ);
         this.aquiferSampler = AquiferSampler.aquifer(this, new ChunkPos(k, l), noiseRouter2, noiseConfig.getAquiferRandomDeriver(), generationShapeConfig.minimumY(), generationShapeConfig.height(), fluidLevelSampler);
      }

      List list = new ArrayList();
      DensityFunction densityFunction = DensityFunctionTypes.cacheAllInCell(DensityFunctionTypes.add(noiseRouter2.finalDensity(), DensityFunctionTypes.Beardifier.INSTANCE)).apply(this::getActualDensityFunction);
      list.add((pos) -> {
         return this.aquiferSampler.apply(pos, densityFunction.sample(pos));
      });
      if (chunkGeneratorSettings.oreVeins()) {
         list.add(OreVeinSampler.create(noiseRouter2.veinToggle(), noiseRouter2.veinRidged(), noiseRouter2.veinGap(), noiseConfig.getOreRandomDeriver()));
      }

      this.blockStateSampler = new ChainedBlockSource((BlockStateSampler[])list.toArray(new BlockStateSampler[0]));
      this.initialDensityWithoutJaggedness = noiseRouter2.initialDensityWithoutJaggedness();
   }

   protected MultiNoiseUtil.MultiNoiseSampler createMultiNoiseSampler(NoiseRouter noiseRouter, List spawnTarget) {
      return new MultiNoiseUtil.MultiNoiseSampler(noiseRouter.temperature().apply(this::getActualDensityFunction), noiseRouter.vegetation().apply(this::getActualDensityFunction), noiseRouter.continents().apply(this::getActualDensityFunction), noiseRouter.erosion().apply(this::getActualDensityFunction), noiseRouter.depth().apply(this::getActualDensityFunction), noiseRouter.ridges().apply(this::getActualDensityFunction), spawnTarget);
   }

   @Nullable
   protected BlockState sampleBlockState() {
      return this.blockStateSampler.sample(this);
   }

   public int blockX() {
      return this.startBlockX + this.cellBlockX;
   }

   public int blockY() {
      return this.startBlockY + this.cellBlockY;
   }

   public int blockZ() {
      return this.startBlockZ + this.cellBlockZ;
   }

   public int estimateSurfaceHeight(int blockX, int blockZ) {
      int i = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockX));
      int j = BiomeCoords.toBlock(BiomeCoords.fromBlock(blockZ));
      return this.surfaceHeightEstimateCache.computeIfAbsent(ColumnPos.pack(i, j), this::calculateSurfaceHeightEstimate);
   }

   private int calculateSurfaceHeightEstimate(long columnPos) {
      int i = ColumnPos.getX(columnPos);
      int j = ColumnPos.getZ(columnPos);
      int k = this.generationShapeConfig.minimumY();

      for(int l = k + this.generationShapeConfig.height(); l >= k; l -= this.verticalCellBlockCount) {
         if (this.initialDensityWithoutJaggedness.sample(new DensityFunction.UnblendedNoisePos(i, l, j)) > 0.390625) {
            return l;
         }
      }

      return Integer.MAX_VALUE;
   }

   public Blender getBlender() {
      return this.blender;
   }

   private void sampleDensity(boolean start, int cellX) {
      this.startBlockX = cellX * this.horizontalCellBlockCount;
      this.cellBlockX = 0;

      for(int i = 0; i < this.horizontalCellCount + 1; ++i) {
         int j = this.startCellZ + i;
         this.startBlockZ = j * this.horizontalCellBlockCount;
         this.cellBlockZ = 0;
         ++this.cacheOnceUniqueIndex;
         Iterator var5 = this.interpolators.iterator();

         while(var5.hasNext()) {
            DensityInterpolator densityInterpolator = (DensityInterpolator)var5.next();
            double[] ds = (start ? densityInterpolator.startDensityBuffer : densityInterpolator.endDensityBuffer)[i];
            densityInterpolator.fill(ds, this.interpolationEachApplier);
         }
      }

      ++this.cacheOnceUniqueIndex;
   }

   public void sampleStartDensity() {
      if (this.isInInterpolationLoop) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.isInInterpolationLoop = true;
         this.sampleUniqueIndex = 0L;
         this.sampleDensity(true, this.startCellX);
      }
   }

   public void sampleEndDensity(int cellX) {
      this.sampleDensity(false, this.startCellX + cellX + 1);
      this.startBlockX = (this.startCellX + cellX) * this.horizontalCellBlockCount;
   }

   public ChunkNoiseSampler at(int i) {
      int j = Math.floorMod(i, this.horizontalCellBlockCount);
      int k = Math.floorDiv(i, this.horizontalCellBlockCount);
      int l = Math.floorMod(k, this.horizontalCellBlockCount);
      int m = this.verticalCellBlockCount - 1 - Math.floorDiv(k, this.horizontalCellBlockCount);
      this.cellBlockX = l;
      this.cellBlockY = m;
      this.cellBlockZ = j;
      this.index = i;
      return this;
   }

   public void fill(double[] densities, DensityFunction densityFunction) {
      this.index = 0;

      for(int i = this.verticalCellBlockCount - 1; i >= 0; --i) {
         this.cellBlockY = i;

         for(int j = 0; j < this.horizontalCellBlockCount; ++j) {
            this.cellBlockX = j;

            for(int k = 0; k < this.horizontalCellBlockCount; ++k) {
               this.cellBlockZ = k;
               densities[this.index++] = densityFunction.sample(this);
            }
         }
      }

   }

   public void onSampledCellCorners(int cellY, int cellZ) {
      Iterator var3 = this.interpolators.iterator();

      while(var3.hasNext()) {
         DensityInterpolator densityInterpolator = (DensityInterpolator)var3.next();
         densityInterpolator.onSampledCellCorners(cellY, cellZ);
      }

      this.isSamplingForCaches = true;
      this.startBlockY = (cellY + this.minimumCellY) * this.verticalCellBlockCount;
      this.startBlockZ = (this.startCellZ + cellZ) * this.horizontalCellBlockCount;
      ++this.cacheOnceUniqueIndex;
      var3 = this.caches.iterator();

      while(var3.hasNext()) {
         CellCache cellCache = (CellCache)var3.next();
         cellCache.delegate.fill(cellCache.cache, this);
      }

      ++this.cacheOnceUniqueIndex;
      this.isSamplingForCaches = false;
   }

   public void interpolateY(int blockY, double deltaY) {
      this.cellBlockY = blockY - this.startBlockY;
      Iterator var4 = this.interpolators.iterator();

      while(var4.hasNext()) {
         DensityInterpolator densityInterpolator = (DensityInterpolator)var4.next();
         densityInterpolator.interpolateY(deltaY);
      }

   }

   public void interpolateX(int blockX, double deltaX) {
      this.cellBlockX = blockX - this.startBlockX;
      Iterator var4 = this.interpolators.iterator();

      while(var4.hasNext()) {
         DensityInterpolator densityInterpolator = (DensityInterpolator)var4.next();
         densityInterpolator.interpolateX(deltaX);
      }

   }

   public void interpolateZ(int blockZ, double deltaZ) {
      this.cellBlockZ = blockZ - this.startBlockZ;
      ++this.sampleUniqueIndex;
      Iterator var4 = this.interpolators.iterator();

      while(var4.hasNext()) {
         DensityInterpolator densityInterpolator = (DensityInterpolator)var4.next();
         densityInterpolator.interpolateZ(deltaZ);
      }

   }

   public void stopInterpolation() {
      if (!this.isInInterpolationLoop) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.isInInterpolationLoop = false;
      }
   }

   public void swapBuffers() {
      this.interpolators.forEach(DensityInterpolator::swapBuffers);
   }

   public AquiferSampler getAquiferSampler() {
      return this.aquiferSampler;
   }

   protected int getHorizontalCellBlockCount() {
      return this.horizontalCellBlockCount;
   }

   protected int getVerticalCellBlockCount() {
      return this.verticalCellBlockCount;
   }

   Blender.BlendResult calculateBlendResult(int blockX, int blockZ) {
      long l = ChunkPos.toLong(blockX, blockZ);
      if (this.lastBlendingColumnPos == l) {
         return this.lastBlendingResult;
      } else {
         this.lastBlendingColumnPos = l;
         Blender.BlendResult blendResult = this.blender.calculate(blockX, blockZ);
         this.lastBlendingResult = blendResult;
         return blendResult;
      }
   }

   protected DensityFunction getActualDensityFunction(DensityFunction function) {
      return (DensityFunction)this.actualDensityFunctionCache.computeIfAbsent(function, this::getActualDensityFunctionImpl);
   }

   private DensityFunction getActualDensityFunctionImpl(DensityFunction function) {
      if (function instanceof DensityFunctionTypes.Wrapping) {
         DensityFunctionTypes.Wrapping wrapping = (DensityFunctionTypes.Wrapping)function;
         Object var10000;
         switch (wrapping.type()) {
            case INTERPOLATED:
               var10000 = new DensityInterpolator(wrapping.wrapped());
               break;
            case FLAT_CACHE:
               var10000 = new FlatCache(wrapping.wrapped(), true);
               break;
            case CACHE2D:
               var10000 = new Cache2D(wrapping.wrapped());
               break;
            case CACHE_ONCE:
               var10000 = new CacheOnce(wrapping.wrapped());
               break;
            case CACHE_ALL_IN_CELL:
               var10000 = new CellCache(wrapping.wrapped());
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return (DensityFunction)var10000;
      } else {
         if (this.blender != Blender.getNoBlending()) {
            if (function == DensityFunctionTypes.BlendAlpha.INSTANCE) {
               return this.cachedBlendAlphaDensityFunction;
            }

            if (function == DensityFunctionTypes.BlendOffset.INSTANCE) {
               return this.cachedBlendOffsetDensityFunction;
            }
         }

         if (function == DensityFunctionTypes.Beardifier.INSTANCE) {
            return this.beardifying;
         } else if (function instanceof DensityFunctionTypes.RegistryEntryHolder) {
            DensityFunctionTypes.RegistryEntryHolder registryEntryHolder = (DensityFunctionTypes.RegistryEntryHolder)function;
            return (DensityFunction)registryEntryHolder.function().value();
         } else {
            return function;
         }
      }
   }

   // $FF: synthetic method
   public DensityFunction.NoisePos at(final int index) {
      return this.at(index);
   }

   class FlatCache implements DensityFunctionTypes.Wrapper, ParentedNoiseType {
      private final DensityFunction delegate;
      final double[][] cache;

      FlatCache(final DensityFunction delegate, final boolean sample) {
         this.delegate = delegate;
         this.cache = new double[ChunkNoiseSampler.this.horizontalBiomeEnd + 1][ChunkNoiseSampler.this.horizontalBiomeEnd + 1];
         if (sample) {
            for(int i = 0; i <= ChunkNoiseSampler.this.horizontalBiomeEnd; ++i) {
               int j = ChunkNoiseSampler.this.startBiomeX + i;
               int k = BiomeCoords.toBlock(j);

               for(int l = 0; l <= ChunkNoiseSampler.this.horizontalBiomeEnd; ++l) {
                  int m = ChunkNoiseSampler.this.startBiomeZ + l;
                  int n = BiomeCoords.toBlock(m);
                  this.cache[i][l] = delegate.sample(new DensityFunction.UnblendedNoisePos(k, 0, n));
               }
            }
         }

      }

      public double sample(DensityFunction.NoisePos pos) {
         int i = BiomeCoords.fromBlock(pos.blockX());
         int j = BiomeCoords.fromBlock(pos.blockZ());
         int k = i - ChunkNoiseSampler.this.startBiomeX;
         int l = j - ChunkNoiseSampler.this.startBiomeZ;
         int m = this.cache.length;
         return k >= 0 && l >= 0 && k < m && l < m ? this.cache[k][l] : this.delegate.sample(pos);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public DensityFunction wrapped() {
         return this.delegate;
      }

      public DensityFunctionTypes.Wrapping.Type type() {
         return DensityFunctionTypes.Wrapping.Type.FLAT_CACHE;
      }
   }

   class BlendAlphaDensityFunction implements ParentedNoiseType {
      public DensityFunction wrapped() {
         return DensityFunctionTypes.BlendAlpha.INSTANCE;
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return this.wrapped().apply(visitor);
      }

      public double sample(DensityFunction.NoisePos pos) {
         return ChunkNoiseSampler.this.calculateBlendResult(pos.blockX(), pos.blockZ()).alpha();
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 1.0;
      }

      public CodecHolder getCodecHolder() {
         return DensityFunctionTypes.BlendAlpha.CODEC;
      }
   }

   class BlendOffsetDensityFunction implements ParentedNoiseType {
      public DensityFunction wrapped() {
         return DensityFunctionTypes.BlendOffset.INSTANCE;
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return this.wrapped().apply(visitor);
      }

      public double sample(DensityFunction.NoisePos pos) {
         return ChunkNoiseSampler.this.calculateBlendResult(pos.blockX(), pos.blockZ()).blendingOffset();
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public double minValue() {
         return Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return Double.POSITIVE_INFINITY;
      }

      public CodecHolder getCodecHolder() {
         return DensityFunctionTypes.BlendOffset.CODEC;
      }
   }

   @FunctionalInterface
   public interface BlockStateSampler {
      @Nullable
      BlockState sample(DensityFunction.NoisePos pos);
   }

   public class DensityInterpolator implements DensityFunctionTypes.Wrapper, ParentedNoiseType {
      double[][] startDensityBuffer;
      double[][] endDensityBuffer;
      private final DensityFunction delegate;
      private double x0y0z0;
      private double x0y0z1;
      private double x1y0z0;
      private double x1y0z1;
      private double x0y1z0;
      private double x0y1z1;
      private double x1y1z0;
      private double x1y1z1;
      private double x0z0;
      private double x1z0;
      private double x0z1;
      private double x1z1;
      private double z0;
      private double z1;
      private double result;

      DensityInterpolator(final DensityFunction delegate) {
         this.delegate = delegate;
         this.startDensityBuffer = this.createBuffer(ChunkNoiseSampler.this.verticalCellCount, ChunkNoiseSampler.this.horizontalCellCount);
         this.endDensityBuffer = this.createBuffer(ChunkNoiseSampler.this.verticalCellCount, ChunkNoiseSampler.this.horizontalCellCount);
         ChunkNoiseSampler.this.interpolators.add(this);
      }

      private double[][] createBuffer(int sizeZ, int sizeX) {
         int i = sizeX + 1;
         int j = sizeZ + 1;
         double[][] ds = new double[i][j];

         for(int k = 0; k < i; ++k) {
            ds[k] = new double[j];
         }

         return ds;
      }

      void onSampledCellCorners(int cellY, int cellZ) {
         this.x0y0z0 = this.startDensityBuffer[cellZ][cellY];
         this.x0y0z1 = this.startDensityBuffer[cellZ + 1][cellY];
         this.x1y0z0 = this.endDensityBuffer[cellZ][cellY];
         this.x1y0z1 = this.endDensityBuffer[cellZ + 1][cellY];
         this.x0y1z0 = this.startDensityBuffer[cellZ][cellY + 1];
         this.x0y1z1 = this.startDensityBuffer[cellZ + 1][cellY + 1];
         this.x1y1z0 = this.endDensityBuffer[cellZ][cellY + 1];
         this.x1y1z1 = this.endDensityBuffer[cellZ + 1][cellY + 1];
      }

      void interpolateY(double deltaY) {
         this.x0z0 = MathHelper.lerp(deltaY, this.x0y0z0, this.x0y1z0);
         this.x1z0 = MathHelper.lerp(deltaY, this.x1y0z0, this.x1y1z0);
         this.x0z1 = MathHelper.lerp(deltaY, this.x0y0z1, this.x0y1z1);
         this.x1z1 = MathHelper.lerp(deltaY, this.x1y0z1, this.x1y1z1);
      }

      void interpolateX(double deltaX) {
         this.z0 = MathHelper.lerp(deltaX, this.x0z0, this.x1z0);
         this.z1 = MathHelper.lerp(deltaX, this.x0z1, this.x1z1);
      }

      void interpolateZ(double deltaZ) {
         this.result = MathHelper.lerp(deltaZ, this.z0, this.z1);
      }

      public double sample(DensityFunction.NoisePos pos) {
         if (pos != ChunkNoiseSampler.this) {
            return this.delegate.sample(pos);
         } else if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            return ChunkNoiseSampler.this.isSamplingForCaches ? MathHelper.lerp3((double)ChunkNoiseSampler.this.cellBlockX / (double)ChunkNoiseSampler.this.horizontalCellBlockCount, (double)ChunkNoiseSampler.this.cellBlockY / (double)ChunkNoiseSampler.this.verticalCellBlockCount, (double)ChunkNoiseSampler.this.cellBlockZ / (double)ChunkNoiseSampler.this.horizontalCellBlockCount, this.x0y0z0, this.x1y0z0, this.x0y1z0, this.x1y1z0, this.x0y0z1, this.x1y0z1, this.x0y1z1, this.x1y1z1) : this.result;
         }
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         if (ChunkNoiseSampler.this.isSamplingForCaches) {
            applier.fill(densities, this);
         } else {
            this.wrapped().fill(densities, applier);
         }
      }

      public DensityFunction wrapped() {
         return this.delegate;
      }

      private void swapBuffers() {
         double[][] ds = this.startDensityBuffer;
         this.startDensityBuffer = this.endDensityBuffer;
         this.endDensityBuffer = ds;
      }

      public DensityFunctionTypes.Wrapping.Type type() {
         return DensityFunctionTypes.Wrapping.Type.INTERPOLATED;
      }
   }

   class CellCache implements DensityFunctionTypes.Wrapper, ParentedNoiseType {
      final DensityFunction delegate;
      final double[] cache;

      CellCache(final DensityFunction delegate) {
         this.delegate = delegate;
         this.cache = new double[ChunkNoiseSampler.this.horizontalCellBlockCount * ChunkNoiseSampler.this.horizontalCellBlockCount * ChunkNoiseSampler.this.verticalCellBlockCount];
         ChunkNoiseSampler.this.caches.add(this);
      }

      public double sample(DensityFunction.NoisePos pos) {
         if (pos != ChunkNoiseSampler.this) {
            return this.delegate.sample(pos);
         } else if (!ChunkNoiseSampler.this.isInInterpolationLoop) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            int i = ChunkNoiseSampler.this.cellBlockX;
            int j = ChunkNoiseSampler.this.cellBlockY;
            int k = ChunkNoiseSampler.this.cellBlockZ;
            return i >= 0 && j >= 0 && k >= 0 && i < ChunkNoiseSampler.this.horizontalCellBlockCount && j < ChunkNoiseSampler.this.verticalCellBlockCount && k < ChunkNoiseSampler.this.horizontalCellBlockCount ? this.cache[((ChunkNoiseSampler.this.verticalCellBlockCount - 1 - j) * ChunkNoiseSampler.this.horizontalCellBlockCount + i) * ChunkNoiseSampler.this.horizontalCellBlockCount + k] : this.delegate.sample(pos);
         }
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public DensityFunction wrapped() {
         return this.delegate;
      }

      public DensityFunctionTypes.Wrapping.Type type() {
         return DensityFunctionTypes.Wrapping.Type.CACHE_ALL_IN_CELL;
      }
   }

   static class Cache2D implements DensityFunctionTypes.Wrapper, ParentedNoiseType {
      private final DensityFunction delegate;
      private long lastSamplingColumnPos;
      private double lastSamplingResult;

      Cache2D(DensityFunction delegate) {
         this.lastSamplingColumnPos = ChunkPos.MARKER;
         this.delegate = delegate;
      }

      public double sample(DensityFunction.NoisePos pos) {
         int i = pos.blockX();
         int j = pos.blockZ();
         long l = ChunkPos.toLong(i, j);
         if (this.lastSamplingColumnPos == l) {
            return this.lastSamplingResult;
         } else {
            this.lastSamplingColumnPos = l;
            double d = this.delegate.sample(pos);
            this.lastSamplingResult = d;
            return d;
         }
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.delegate.fill(densities, applier);
      }

      public DensityFunction wrapped() {
         return this.delegate;
      }

      public DensityFunctionTypes.Wrapping.Type type() {
         return DensityFunctionTypes.Wrapping.Type.CACHE2D;
      }
   }

   class CacheOnce implements DensityFunctionTypes.Wrapper, ParentedNoiseType {
      private final DensityFunction delegate;
      private long sampleUniqueIndex;
      private long cacheOnceUniqueIndex;
      private double lastSamplingResult;
      @Nullable
      private double[] cache;

      CacheOnce(final DensityFunction delegate) {
         this.delegate = delegate;
      }

      public double sample(DensityFunction.NoisePos pos) {
         if (pos != ChunkNoiseSampler.this) {
            return this.delegate.sample(pos);
         } else if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
            return this.cache[ChunkNoiseSampler.this.index];
         } else if (this.sampleUniqueIndex == ChunkNoiseSampler.this.sampleUniqueIndex) {
            return this.lastSamplingResult;
         } else {
            this.sampleUniqueIndex = ChunkNoiseSampler.this.sampleUniqueIndex;
            double d = this.delegate.sample(pos);
            this.lastSamplingResult = d;
            return d;
         }
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         if (this.cache != null && this.cacheOnceUniqueIndex == ChunkNoiseSampler.this.cacheOnceUniqueIndex) {
            System.arraycopy(this.cache, 0, densities, 0, densities.length);
         } else {
            this.wrapped().fill(densities, applier);
            if (this.cache != null && this.cache.length == densities.length) {
               System.arraycopy(densities, 0, this.cache, 0, densities.length);
            } else {
               this.cache = (double[])densities.clone();
            }

            this.cacheOnceUniqueIndex = ChunkNoiseSampler.this.cacheOnceUniqueIndex;
         }
      }

      public DensityFunction wrapped() {
         return this.delegate;
      }

      public DensityFunctionTypes.Wrapping.Type type() {
         return DensityFunctionTypes.Wrapping.Type.CACHE_ONCE;
      }
   }

   private interface ParentedNoiseType extends DensityFunction {
      DensityFunction wrapped();

      default double minValue() {
         return this.wrapped().minValue();
      }

      default double maxValue() {
         return this.wrapped().maxValue();
      }
   }
}
