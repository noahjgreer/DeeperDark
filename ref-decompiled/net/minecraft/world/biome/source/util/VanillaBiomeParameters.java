package net.minecraft.world.biome.source.util;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.Spline;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public final class VanillaBiomeParameters {
   private static final float MAX_VALLEY_WEIRDNESS = 0.05F;
   private static final float MAX_LOW_WEIRDNESS = 0.26666668F;
   public static final float MAX_MID_WEIRDNESS = 0.4F;
   private static final float MAX_SECOND_HIGH_WEIRDNESS = 0.93333334F;
   private static final float field_34501 = 0.1F;
   public static final float MAX_HIGH_WEIRDNESS = 0.56666666F;
   private static final float MAX_PEAK_WEIRDNESS = 0.7666667F;
   public static final float field_35042 = -0.11F;
   public static final float field_35043 = 0.03F;
   public static final float field_35044 = 0.3F;
   public static final float field_35045 = -0.78F;
   public static final float field_35046 = -0.375F;
   private static final float field_39134 = -0.225F;
   private static final float field_39135 = 0.9F;
   private final MultiNoiseUtil.ParameterRange defaultParameter = MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F);
   private final MultiNoiseUtil.ParameterRange[] temperatureParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0F, -0.45F), MultiNoiseUtil.ParameterRange.of(-0.45F, -0.15F), MultiNoiseUtil.ParameterRange.of(-0.15F, 0.2F), MultiNoiseUtil.ParameterRange.of(0.2F, 0.55F), MultiNoiseUtil.ParameterRange.of(0.55F, 1.0F)};
   private final MultiNoiseUtil.ParameterRange[] humidityParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0F, -0.35F), MultiNoiseUtil.ParameterRange.of(-0.35F, -0.1F), MultiNoiseUtil.ParameterRange.of(-0.1F, 0.1F), MultiNoiseUtil.ParameterRange.of(0.1F, 0.3F), MultiNoiseUtil.ParameterRange.of(0.3F, 1.0F)};
   private final MultiNoiseUtil.ParameterRange[] erosionParameters = new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-1.0F, -0.78F), MultiNoiseUtil.ParameterRange.of(-0.78F, -0.375F), MultiNoiseUtil.ParameterRange.of(-0.375F, -0.2225F), MultiNoiseUtil.ParameterRange.of(-0.2225F, 0.05F), MultiNoiseUtil.ParameterRange.of(0.05F, 0.45F), MultiNoiseUtil.ParameterRange.of(0.45F, 0.55F), MultiNoiseUtil.ParameterRange.of(0.55F, 1.0F)};
   private final MultiNoiseUtil.ParameterRange frozenTemperature;
   private final MultiNoiseUtil.ParameterRange nonFrozenTemperatureParameters;
   private final MultiNoiseUtil.ParameterRange mushroomFieldsContinentalness;
   private final MultiNoiseUtil.ParameterRange deepOceanContinentalness;
   private final MultiNoiseUtil.ParameterRange oceanContinentalness;
   private final MultiNoiseUtil.ParameterRange coastContinentalness;
   private final MultiNoiseUtil.ParameterRange riverContinentalness;
   private final MultiNoiseUtil.ParameterRange nearInlandContinentalness;
   private final MultiNoiseUtil.ParameterRange midInlandContinentalness;
   private final MultiNoiseUtil.ParameterRange farInlandContinentalness;
   private final RegistryKey[][] oceanBiomes;
   private final RegistryKey[][] commonBiomes;
   private final RegistryKey[][] uncommonBiomes;
   private final RegistryKey[][] nearMountainBiomes;
   private final RegistryKey[][] specialNearMountainBiomes;
   private final RegistryKey[][] windsweptBiomes;

   public VanillaBiomeParameters() {
      this.frozenTemperature = this.temperatureParameters[0];
      this.nonFrozenTemperatureParameters = MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[4]);
      this.mushroomFieldsContinentalness = MultiNoiseUtil.ParameterRange.of(-1.2F, -1.05F);
      this.deepOceanContinentalness = MultiNoiseUtil.ParameterRange.of(-1.05F, -0.455F);
      this.oceanContinentalness = MultiNoiseUtil.ParameterRange.of(-0.455F, -0.19F);
      this.coastContinentalness = MultiNoiseUtil.ParameterRange.of(-0.19F, -0.11F);
      this.riverContinentalness = MultiNoiseUtil.ParameterRange.of(-0.11F, 0.55F);
      this.nearInlandContinentalness = MultiNoiseUtil.ParameterRange.of(-0.11F, 0.03F);
      this.midInlandContinentalness = MultiNoiseUtil.ParameterRange.of(0.03F, 0.3F);
      this.farInlandContinentalness = MultiNoiseUtil.ParameterRange.of(0.3F, 1.0F);
      this.oceanBiomes = new RegistryKey[][]{{BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.DEEP_COLD_OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}, {BiomeKeys.FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.WARM_OCEAN}};
      this.commonBiomes = new RegistryKey[][]{{BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.TAIGA}, {BiomeKeys.PLAINS, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA}, {BiomeKeys.FLOWER_FOREST, BiomeKeys.PLAINS, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST}, {BiomeKeys.SAVANNA, BiomeKeys.SAVANNA, BiomeKeys.FOREST, BiomeKeys.JUNGLE, BiomeKeys.JUNGLE}, {BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT, BiomeKeys.DESERT}};
      this.uncommonBiomes = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, BiomeKeys.SNOWY_TAIGA, null, null}, {null, null, null, null, BiomeKeys.OLD_GROWTH_PINE_TAIGA}, {BiomeKeys.SUNFLOWER_PLAINS, null, null, BiomeKeys.OLD_GROWTH_BIRCH_FOREST, null}, {null, null, BiomeKeys.PLAINS, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE}, {null, null, null, null, null}};
      this.nearMountainBiomes = new RegistryKey[][]{{BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.FOREST, BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA}, {BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.PALE_GARDEN}, {BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.FOREST, BiomeKeys.FOREST, BiomeKeys.JUNGLE}, {BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.BADLANDS, BiomeKeys.WOODED_BADLANDS, BiomeKeys.WOODED_BADLANDS}};
      this.specialNearMountainBiomes = new RegistryKey[][]{{BiomeKeys.ICE_SPIKES, null, null, null, null}, {BiomeKeys.CHERRY_GROVE, null, BiomeKeys.MEADOW, BiomeKeys.MEADOW, BiomeKeys.OLD_GROWTH_PINE_TAIGA}, {BiomeKeys.CHERRY_GROVE, BiomeKeys.CHERRY_GROVE, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, null}, {null, null, null, null, null}, {BiomeKeys.ERODED_BADLANDS, BiomeKeys.ERODED_BADLANDS, null, null, null}};
      this.windsweptBiomes = new RegistryKey[][]{{BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_HILLS, BiomeKeys.WINDSWEPT_FOREST, BiomeKeys.WINDSWEPT_FOREST}, {null, null, null, null, null}, {null, null, null, null, null}};
   }

   public List getSpawnSuitabilityNoises() {
      MultiNoiseUtil.ParameterRange parameterRange = MultiNoiseUtil.ParameterRange.of(0.0F);
      float f = 0.16F;
      return List.of(new MultiNoiseUtil.NoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.defaultParameter), this.defaultParameter, parameterRange, MultiNoiseUtil.ParameterRange.of(-1.0F, -0.16F), 0L), new MultiNoiseUtil.NoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.defaultParameter), this.defaultParameter, parameterRange, MultiNoiseUtil.ParameterRange.of(0.16F, 1.0F), 0L));
   }

   protected void writeOverworldBiomeParameters(Consumer parameters) {
      if (SharedConstants.DEBUG_BIOME_SOURCE) {
         this.writeDebug(parameters);
      } else {
         this.writeOceanBiomes(parameters);
         this.writeLandBiomes(parameters);
         this.writeCaveBiomes(parameters);
      }
   }

   private void writeDebug(Consumer parameters) {
      RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();
      RegistryEntryLookup registryEntryLookup = wrapperLookup.getOrThrow(RegistryKeys.DENSITY_FUNCTION);
      DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.CONTINENTS_OVERWORLD));
      DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper2 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.EROSION_OVERWORLD));
      DensityFunctionTypes.Spline.DensityFunctionWrapper densityFunctionWrapper3 = new DensityFunctionTypes.Spline.DensityFunctionWrapper(registryEntryLookup.getOrThrow(DensityFunctions.RIDGES_FOLDED_OVERWORLD));
      parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.0F), this.defaultParameter, 0.01F), BiomeKeys.PLAINS));
      Spline spline = VanillaTerrainParametersCreator.createContinentalOffsetSpline(densityFunctionWrapper2, densityFunctionWrapper3, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, ToFloatFunction.IDENTITY);
      float[] var10;
      int var11;
      int var12;
      float f;
      if (spline instanceof Spline.Implementation implementation) {
         RegistryKey registryKey = BiomeKeys.DESERT;
         var10 = implementation.locations();
         var11 = var10.length;

         for(var12 = 0; var12 < var11; ++var12) {
            f = var10[var12];
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(f), MultiNoiseUtil.ParameterRange.of(0.0F), this.defaultParameter, 0.0F), registryKey));
            registryKey = registryKey == BiomeKeys.DESERT ? BiomeKeys.BADLANDS : BiomeKeys.DESERT;
         }
      }

      Spline spline2 = VanillaTerrainParametersCreator.createOffsetSpline(densityFunctionWrapper, densityFunctionWrapper2, densityFunctionWrapper3, false);
      if (spline2 instanceof Spline.Implementation implementation2) {
         var10 = implementation2.locations();
         var11 = var10.length;

         for(var12 = 0; var12 < var11; ++var12) {
            f = var10[var12];
            parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(f), this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.0F), this.defaultParameter, 0.0F), BiomeKeys.SNOWY_TAIGA));
         }
      }

   }

   private void writeOceanBiomes(Consumer parameters) {
      this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.mushroomFieldsContinentalness, this.defaultParameter, this.defaultParameter, 0.0F, BiomeKeys.MUSHROOM_FIELDS);

      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];
         this.writeBiomeParameters(parameters, parameterRange, this.defaultParameter, this.deepOceanContinentalness, this.defaultParameter, this.defaultParameter, 0.0F, this.oceanBiomes[0][i]);
         this.writeBiomeParameters(parameters, parameterRange, this.defaultParameter, this.oceanContinentalness, this.defaultParameter, this.defaultParameter, 0.0F, this.oceanBiomes[1][i]);
      }

   }

   private void writeLandBiomes(Consumer parameters) {
      this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-1.0F, -0.93333334F));
      this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.93333334F, -0.7666667F));
      this.writePeakBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.7666667F, -0.56666666F));
      this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.56666666F, -0.4F));
      this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.4F, -0.26666668F));
      this.writeLowBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.26666668F, -0.05F));
      this.writeValleyBiomes(parameters, MultiNoiseUtil.ParameterRange.of(-0.05F, 0.05F));
      this.writeLowBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.05F, 0.26666668F));
      this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.26666668F, 0.4F));
      this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.4F, 0.56666666F));
      this.writePeakBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.56666666F, 0.7666667F));
      this.writeHighBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.7666667F, 0.93333334F));
      this.writeMidBiomes(parameters, MultiNoiseUtil.ParameterRange.of(0.93333334F, 1.0F));
   }

   private void writePeakBiomes(Consumer parameters, MultiNoiseUtil.ParameterRange weirdness) {
      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];

         for(int j = 0; j < this.humidityParameters.length; ++j) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
            RegistryKey registryKey = this.getRegularBiome(i, j, weirdness);
            RegistryKey registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey3 = this.getMountainStartBiome(i, j, weirdness);
            RegistryKey registryKey4 = this.getNearMountainBiome(i, j, weirdness);
            RegistryKey registryKey5 = this.getWindsweptOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey6 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey5);
            RegistryKey registryKey7 = this.getPeakBiome(i, j, weirdness);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0F, registryKey7);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[1], weirdness, 0.0F, registryKey3);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[1], weirdness, 0.0F, registryKey7);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[2], weirdness, 0.0F, registryKey4);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[3], weirdness, 0.0F, registryKey2);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[3], weirdness, 0.0F, registryKey4);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey6);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey5);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, registryKey);
         }
      }

   }

   private void writeHighBiomes(Consumer parameters, MultiNoiseUtil.ParameterRange weirdness) {
      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];

         for(int j = 0; j < this.humidityParameters.length; ++j) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
            RegistryKey registryKey = this.getRegularBiome(i, j, weirdness);
            RegistryKey registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey3 = this.getMountainStartBiome(i, j, weirdness);
            RegistryKey registryKey4 = this.getNearMountainBiome(i, j, weirdness);
            RegistryKey registryKey5 = this.getWindsweptOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey6 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
            RegistryKey registryKey7 = this.getMountainSlopeBiome(i, j, weirdness);
            RegistryKey registryKey8 = this.getPeakBiome(i, j, weirdness);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[0], weirdness, 0.0F, registryKey7);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0F, registryKey8);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[1], weirdness, 0.0F, registryKey3);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[1], weirdness, 0.0F, registryKey7);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[2], weirdness, 0.0F, registryKey4);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[3], weirdness, 0.0F, registryKey2);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[3], weirdness, 0.0F, registryKey4);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey6);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey5);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, registryKey);
         }
      }

   }

   private void writeMidBiomes(Consumer parameters, MultiNoiseUtil.ParameterRange weirdness) {
      this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[2]), weirdness, 0.0F, BiomeKeys.STONY_SHORE);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.SWAMP);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.MANGROVE_SWAMP);

      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];

         for(int j = 0; j < this.humidityParameters.length; ++j) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
            RegistryKey registryKey = this.getRegularBiome(i, j, weirdness);
            RegistryKey registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey3 = this.getMountainStartBiome(i, j, weirdness);
            RegistryKey registryKey4 = this.getWindsweptOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey5 = this.getNearMountainBiome(i, j, weirdness);
            RegistryKey registryKey6 = this.getShoreBiome(i, j);
            RegistryKey registryKey7 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
            RegistryKey registryKey8 = this.getErodedShoreBiome(i, j, weirdness);
            RegistryKey registryKey9 = this.getMountainSlopeBiome(i, j, weirdness);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[0], weirdness, 0.0F, registryKey9);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosionParameters[1], weirdness, 0.0F, registryKey3);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[1], weirdness, 0.0F, i == 0 ? registryKey9 : registryKey5);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[2], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.midInlandContinentalness, this.erosionParameters[2], weirdness, 0.0F, registryKey2);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.farInlandContinentalness, this.erosionParameters[2], weirdness, 0.0F, registryKey5);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness), this.erosionParameters[3], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[3], weirdness, 0.0F, registryKey2);
            if (weirdness.max() < 0L) {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[4], weirdness, 0.0F, registryKey6);
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0F, registryKey);
            } else {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0F, registryKey);
            }

            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[5], weirdness, 0.0F, registryKey8);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[5], weirdness, 0.0F, registryKey7);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey4);
            if (weirdness.max() < 0L) {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0F, registryKey6);
            } else {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0F, registryKey);
            }

            if (i == 0) {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, registryKey);
            }
         }
      }

   }

   private void writeLowBiomes(Consumer parameters, MultiNoiseUtil.ParameterRange weirdness) {
      this.writeBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[2]), weirdness, 0.0F, BiomeKeys.STONY_SHORE);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.SWAMP);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.MANGROVE_SWAMP);

      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];

         for(int j = 0; j < this.humidityParameters.length; ++j) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
            RegistryKey registryKey = this.getRegularBiome(i, j, weirdness);
            RegistryKey registryKey2 = this.getBadlandsOrRegularBiome(i, j, weirdness);
            RegistryKey registryKey3 = this.getMountainStartBiome(i, j, weirdness);
            RegistryKey registryKey4 = this.getShoreBiome(i, j);
            RegistryKey registryKey5 = this.getBiomeOrWindsweptSavanna(i, j, weirdness, registryKey);
            RegistryKey registryKey6 = this.getErodedShoreBiome(i, j, weirdness);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, registryKey2);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, registryKey3);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[3]), weirdness, 0.0F, registryKey2);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[3], this.erosionParameters[4]), weirdness, 0.0F, registryKey4);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[4], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[5], weirdness, 0.0F, registryKey6);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.nearInlandContinentalness, this.erosionParameters[5], weirdness, 0.0F, registryKey5);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[5], weirdness, 0.0F, registryKey);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0F, registryKey4);
            if (i == 0) {
               this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, registryKey);
            }
         }
      }

   }

   private void writeValleyBiomes(Consumer parameters, MultiNoiseUtil.ParameterRange weirdness) {
      this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, weirdness.max() < 0L ? BiomeKeys.STONY_SHORE : BiomeKeys.FROZEN_RIVER);
      this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.coastContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, weirdness.max() < 0L ? BiomeKeys.STONY_SHORE : BiomeKeys.RIVER);
      this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, BiomeKeys.FROZEN_RIVER);
      this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.nearInlandContinentalness, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, BiomeKeys.RIVER);
      this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[5]), weirdness, 0.0F, BiomeKeys.FROZEN_RIVER);
      this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[2], this.erosionParameters[5]), weirdness, 0.0F, BiomeKeys.RIVER);
      this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.FROZEN_RIVER);
      this.writeBiomeParameters(parameters, this.nonFrozenTemperatureParameters, this.defaultParameter, this.coastContinentalness, this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.RIVER);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[1], this.temperatureParameters[2]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.SWAMP);
      this.writeBiomeParameters(parameters, MultiNoiseUtil.ParameterRange.combine(this.temperatureParameters[3], this.temperatureParameters[4]), this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.MANGROVE_SWAMP);
      this.writeBiomeParameters(parameters, this.frozenTemperature, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.riverContinentalness, this.farInlandContinentalness), this.erosionParameters[6], weirdness, 0.0F, BiomeKeys.FROZEN_RIVER);

      for(int i = 0; i < this.temperatureParameters.length; ++i) {
         MultiNoiseUtil.ParameterRange parameterRange = this.temperatureParameters[i];

         for(int j = 0; j < this.humidityParameters.length; ++j) {
            MultiNoiseUtil.ParameterRange parameterRange2 = this.humidityParameters[j];
            RegistryKey registryKey = this.getBadlandsOrRegularBiome(i, j, weirdness);
            this.writeBiomeParameters(parameters, parameterRange, parameterRange2, MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness), MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), weirdness, 0.0F, registryKey);
         }
      }

   }

   private void writeCaveBiomes(Consumer parameters) {
      this.writeCaveBiomeParameters(parameters, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.8F, 1.0F), this.defaultParameter, this.defaultParameter, 0.0F, BiomeKeys.DRIPSTONE_CAVES);
      this.writeCaveBiomeParameters(parameters, this.defaultParameter, MultiNoiseUtil.ParameterRange.of(0.7F, 1.0F), this.defaultParameter, this.defaultParameter, this.defaultParameter, 0.0F, BiomeKeys.LUSH_CAVES);
      this.writeDeepDarkParameters(parameters, this.defaultParameter, this.defaultParameter, this.defaultParameter, MultiNoiseUtil.ParameterRange.combine(this.erosionParameters[0], this.erosionParameters[1]), this.defaultParameter, 0.0F, BiomeKeys.DEEP_DARK);
   }

   private RegistryKey getRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      if (weirdness.max() < 0L) {
         return this.commonBiomes[temperature][humidity];
      } else {
         RegistryKey registryKey = this.uncommonBiomes[temperature][humidity];
         return registryKey == null ? this.commonBiomes[temperature][humidity] : registryKey;
      }
   }

   private RegistryKey getBadlandsOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      return temperature == 4 ? this.getBadlandsBiome(humidity, weirdness) : this.getRegularBiome(temperature, humidity, weirdness);
   }

   private RegistryKey getMountainStartBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      return temperature == 0 ? this.getMountainSlopeBiome(temperature, humidity, weirdness) : this.getBadlandsOrRegularBiome(temperature, humidity, weirdness);
   }

   private RegistryKey getBiomeOrWindsweptSavanna(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness, RegistryKey biomeKey) {
      return temperature > 1 && humidity < 4 && weirdness.max() >= 0L ? BiomeKeys.WINDSWEPT_SAVANNA : biomeKey;
   }

   private RegistryKey getErodedShoreBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      RegistryKey registryKey = weirdness.max() >= 0L ? this.getRegularBiome(temperature, humidity, weirdness) : this.getShoreBiome(temperature, humidity);
      return this.getBiomeOrWindsweptSavanna(temperature, humidity, weirdness, registryKey);
   }

   private RegistryKey getShoreBiome(int temperature, int humidity) {
      if (temperature == 0) {
         return BiomeKeys.SNOWY_BEACH;
      } else {
         return temperature == 4 ? BiomeKeys.DESERT : BiomeKeys.BEACH;
      }
   }

   private RegistryKey getBadlandsBiome(int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      if (humidity < 2) {
         return weirdness.max() < 0L ? BiomeKeys.BADLANDS : BiomeKeys.ERODED_BADLANDS;
      } else {
         return humidity < 3 ? BiomeKeys.BADLANDS : BiomeKeys.WOODED_BADLANDS;
      }
   }

   private RegistryKey getNearMountainBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      if (weirdness.max() >= 0L) {
         RegistryKey registryKey = this.specialNearMountainBiomes[temperature][humidity];
         if (registryKey != null) {
            return registryKey;
         }
      }

      return this.nearMountainBiomes[temperature][humidity];
   }

   private RegistryKey getPeakBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      if (temperature <= 2) {
         return weirdness.max() < 0L ? BiomeKeys.JAGGED_PEAKS : BiomeKeys.FROZEN_PEAKS;
      } else {
         return temperature == 3 ? BiomeKeys.STONY_PEAKS : this.getBadlandsBiome(humidity, weirdness);
      }
   }

   private RegistryKey getMountainSlopeBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      if (temperature >= 3) {
         return this.getNearMountainBiome(temperature, humidity, weirdness);
      } else {
         return humidity <= 1 ? BiomeKeys.SNOWY_SLOPES : BiomeKeys.GROVE;
      }
   }

   private RegistryKey getWindsweptOrRegularBiome(int temperature, int humidity, MultiNoiseUtil.ParameterRange weirdness) {
      RegistryKey registryKey = this.windsweptBiomes[temperature][humidity];
      return registryKey == null ? this.getRegularBiome(temperature, humidity, weirdness) : registryKey;
   }

   private void writeBiomeParameters(Consumer parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey biome) {
      parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.0F), weirdness, offset), biome));
      parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.0F), weirdness, offset), biome));
   }

   private void writeCaveBiomeParameters(Consumer parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey biome) {
      parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(0.2F, 0.9F), weirdness, offset), biome));
   }

   private void writeDeepDarkParameters(Consumer parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey biome) {
      parameters.accept(Pair.of(MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, MultiNoiseUtil.ParameterRange.of(1.1F), weirdness, offset), biome));
   }

   public static boolean inDeepDarkParameters(DensityFunction erosion, DensityFunction depth, DensityFunction.NoisePos pos) {
      return erosion.sample(pos) < -0.22499999403953552 && depth.sample(pos) > 0.8999999761581421;
   }

   public static String getPeaksValleysDescription(double weirdness) {
      if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.05F)) {
         return "Valley";
      } else if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.26666668F)) {
         return "Low";
      } else if (weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.4F)) {
         return "Mid";
      } else {
         return weirdness < (double)DensityFunctions.getPeaksValleysNoise(0.56666666F) ? "High" : "Peak";
      }
   }

   public String getContinentalnessDescription(double continentalness) {
      double d = (double)MultiNoiseUtil.toLong((float)continentalness);
      if (d < (double)this.mushroomFieldsContinentalness.max()) {
         return "Mushroom fields";
      } else if (d < (double)this.deepOceanContinentalness.max()) {
         return "Deep ocean";
      } else if (d < (double)this.oceanContinentalness.max()) {
         return "Ocean";
      } else if (d < (double)this.coastContinentalness.max()) {
         return "Coast";
      } else if (d < (double)this.nearInlandContinentalness.max()) {
         return "Near inland";
      } else {
         return d < (double)this.midInlandContinentalness.max() ? "Mid inland" : "Far inland";
      }
   }

   public String getErosionDescription(double erosion) {
      return getNoiseRangeIndex(erosion, this.erosionParameters);
   }

   public String getTemperatureDescription(double temperature) {
      return getNoiseRangeIndex(temperature, this.temperatureParameters);
   }

   public String getHumidityDescription(double humidity) {
      return getNoiseRangeIndex(humidity, this.humidityParameters);
   }

   private static String getNoiseRangeIndex(double noisePoint, MultiNoiseUtil.ParameterRange[] noiseRanges) {
      double d = (double)MultiNoiseUtil.toLong((float)noisePoint);

      for(int i = 0; i < noiseRanges.length; ++i) {
         if (d < (double)noiseRanges[i].max()) {
            return "" + i;
         }
      }

      return "?";
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getTemperatureParameters() {
      return this.temperatureParameters;
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getHumidityParameters() {
      return this.humidityParameters;
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getErosionParameters() {
      return this.erosionParameters;
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getContinentalnessParameters() {
      return new MultiNoiseUtil.ParameterRange[]{this.mushroomFieldsContinentalness, this.deepOceanContinentalness, this.oceanContinentalness, this.coastContinentalness, this.nearInlandContinentalness, this.midInlandContinentalness, this.farInlandContinentalness};
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getPeaksValleysParameters() {
      return new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-2.0F, DensityFunctions.getPeaksValleysNoise(0.05F)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.05F), DensityFunctions.getPeaksValleysNoise(0.26666668F)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.26666668F), DensityFunctions.getPeaksValleysNoise(0.4F)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.4F), DensityFunctions.getPeaksValleysNoise(0.56666666F)), MultiNoiseUtil.ParameterRange.of(DensityFunctions.getPeaksValleysNoise(0.56666666F), 2.0F)};
   }

   @Debug
   public MultiNoiseUtil.ParameterRange[] getWeirdnessParameters() {
      return new MultiNoiseUtil.ParameterRange[]{MultiNoiseUtil.ParameterRange.of(-2.0F, 0.0F), MultiNoiseUtil.ParameterRange.of(0.0F, 2.0F)};
   }
}
