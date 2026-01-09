package net.minecraft.world.gen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(field("barrier", NoiseRouter::barrierNoise), field("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise), field("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise), field("lava", NoiseRouter::lavaNoise), field("temperature", NoiseRouter::temperature), field("vegetation", NoiseRouter::vegetation), field("continents", NoiseRouter::continents), field("erosion", NoiseRouter::erosion), field("depth", NoiseRouter::depth), field("ridges", NoiseRouter::ridges), field("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness), field("final_density", NoiseRouter::finalDensity), field("vein_toggle", NoiseRouter::veinToggle), field("vein_ridged", NoiseRouter::veinRidged), field("vein_gap", NoiseRouter::veinGap)).apply(instance, NoiseRouter::new);
   });

   public NoiseRouter(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, DensityFunction densityFunction4, DensityFunction densityFunction5, DensityFunction densityFunction6, DensityFunction densityFunction7, DensityFunction densityFunction8, DensityFunction densityFunction9, DensityFunction densityFunction10, DensityFunction densityFunction11, DensityFunction densityFunction12, DensityFunction densityFunction13, DensityFunction densityFunction14, DensityFunction densityFunction15) {
      this.barrierNoise = densityFunction;
      this.fluidLevelFloodednessNoise = densityFunction2;
      this.fluidLevelSpreadNoise = densityFunction3;
      this.lavaNoise = densityFunction4;
      this.temperature = densityFunction5;
      this.vegetation = densityFunction6;
      this.continents = densityFunction7;
      this.erosion = densityFunction8;
      this.depth = densityFunction9;
      this.ridges = densityFunction10;
      this.initialDensityWithoutJaggedness = densityFunction11;
      this.finalDensity = densityFunction12;
      this.veinToggle = densityFunction13;
      this.veinRidged = densityFunction14;
      this.veinGap = densityFunction15;
   }

   private static RecordCodecBuilder field(String name, Function getter) {
      return DensityFunction.FUNCTION_CODEC.fieldOf(name).forGetter(getter);
   }

   public NoiseRouter apply(DensityFunction.DensityFunctionVisitor visitor) {
      return new NoiseRouter(this.barrierNoise.apply(visitor), this.fluidLevelFloodednessNoise.apply(visitor), this.fluidLevelSpreadNoise.apply(visitor), this.lavaNoise.apply(visitor), this.temperature.apply(visitor), this.vegetation.apply(visitor), this.continents.apply(visitor), this.erosion.apply(visitor), this.depth.apply(visitor), this.ridges.apply(visitor), this.initialDensityWithoutJaggedness.apply(visitor), this.finalDensity.apply(visitor), this.veinToggle.apply(visitor), this.veinRidged.apply(visitor), this.veinGap.apply(visitor));
   }

   public DensityFunction barrierNoise() {
      return this.barrierNoise;
   }

   public DensityFunction fluidLevelFloodednessNoise() {
      return this.fluidLevelFloodednessNoise;
   }

   public DensityFunction fluidLevelSpreadNoise() {
      return this.fluidLevelSpreadNoise;
   }

   public DensityFunction lavaNoise() {
      return this.lavaNoise;
   }

   public DensityFunction temperature() {
      return this.temperature;
   }

   public DensityFunction vegetation() {
      return this.vegetation;
   }

   public DensityFunction continents() {
      return this.continents;
   }

   public DensityFunction erosion() {
      return this.erosion;
   }

   public DensityFunction depth() {
      return this.depth;
   }

   public DensityFunction ridges() {
      return this.ridges;
   }

   public DensityFunction initialDensityWithoutJaggedness() {
      return this.initialDensityWithoutJaggedness;
   }

   public DensityFunction finalDensity() {
      return this.finalDensity;
   }

   public DensityFunction veinToggle() {
      return this.veinToggle;
   }

   public DensityFunction veinRidged() {
      return this.veinRidged;
   }

   public DensityFunction veinGap() {
      return this.veinGap;
   }
}
