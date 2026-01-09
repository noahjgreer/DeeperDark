package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.chunk.Blender;
import org.jetbrains.annotations.Nullable;

public interface DensityFunction {
   Codec CODEC = DensityFunctionTypes.CODEC;
   Codec REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DENSITY_FUNCTION, CODEC);
   Codec FUNCTION_CODEC = REGISTRY_ENTRY_CODEC.xmap(DensityFunctionTypes.RegistryEntryHolder::new, (function) -> {
      if (function instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder) {
         return registryEntryHolder.function();
      } else {
         return new RegistryEntry.Direct(function);
      }
   });

   double sample(NoisePos pos);

   void fill(double[] densities, EachApplier applier);

   DensityFunction apply(DensityFunctionVisitor visitor);

   double minValue();

   double maxValue();

   CodecHolder getCodecHolder();

   default DensityFunction clamp(double min, double max) {
      return new DensityFunctionTypes.Clamp(this, min, max);
   }

   default DensityFunction abs() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.ABS);
   }

   default DensityFunction square() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.SQUARE);
   }

   default DensityFunction cube() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.CUBE);
   }

   default DensityFunction halfNegative() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.HALF_NEGATIVE);
   }

   default DensityFunction quarterNegative() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.QUARTER_NEGATIVE);
   }

   default DensityFunction squeeze() {
      return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.SQUEEZE);
   }

   public static record UnblendedNoisePos(int blockX, int blockY, int blockZ) implements NoisePos {
      public UnblendedNoisePos(int i, int j, int k) {
         this.blockX = i;
         this.blockY = j;
         this.blockZ = k;
      }

      public int blockX() {
         return this.blockX;
      }

      public int blockY() {
         return this.blockY;
      }

      public int blockZ() {
         return this.blockZ;
      }
   }

   public interface NoisePos {
      int blockX();

      int blockY();

      int blockZ();

      default Blender getBlender() {
         return Blender.getNoBlending();
      }
   }

   public interface Base extends DensityFunction {
      default void fill(double[] densities, EachApplier applier) {
         applier.fill(densities, this);
      }

      default DensityFunction apply(DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)this);
      }
   }

   public interface DensityFunctionVisitor {
      DensityFunction apply(DensityFunction densityFunction);

      default Noise apply(Noise noiseDensityFunction) {
         return noiseDensityFunction;
      }
   }

   public static record Noise(RegistryEntry noiseData, @Nullable DoublePerlinNoiseSampler noise) {
      public static final Codec CODEC;

      public Noise(RegistryEntry noiseData) {
         this(noiseData, (DoublePerlinNoiseSampler)null);
      }

      public Noise(RegistryEntry registryEntry, @Nullable DoublePerlinNoiseSampler doublePerlinNoiseSampler) {
         this.noiseData = registryEntry;
         this.noise = doublePerlinNoiseSampler;
      }

      public double sample(double x, double y, double z) {
         return this.noise == null ? 0.0 : this.noise.sample(x, y, z);
      }

      public double getMaxValue() {
         return this.noise == null ? 2.0 : this.noise.getMaxValue();
      }

      public RegistryEntry noiseData() {
         return this.noiseData;
      }

      @Nullable
      public DoublePerlinNoiseSampler noise() {
         return this.noise;
      }

      static {
         CODEC = DoublePerlinNoiseSampler.NoiseParameters.REGISTRY_ENTRY_CODEC.xmap((noiseData) -> {
            return new Noise(noiseData, (DoublePerlinNoiseSampler)null);
         }, Noise::noiseData);
      }
   }

   public interface EachApplier {
      NoisePos at(int index);

      void fill(double[] densities, DensityFunction densityFunction);
   }
}
