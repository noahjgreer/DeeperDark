package net.minecraft.world.biome.source;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class MultiNoiseBiomeSource extends BiomeSource {
   private static final MapCodec BIOME_CODEC;
   public static final MapCodec CUSTOM_CODEC;
   private static final MapCodec PRESET_CODEC;
   public static final MapCodec CODEC;
   private final Either biomeEntries;

   private MultiNoiseBiomeSource(Either biomeEntries) {
      this.biomeEntries = biomeEntries;
   }

   public static MultiNoiseBiomeSource create(MultiNoiseUtil.Entries biomeEntries) {
      return new MultiNoiseBiomeSource(Either.left(biomeEntries));
   }

   public static MultiNoiseBiomeSource create(RegistryEntry biomeEntries) {
      return new MultiNoiseBiomeSource(Either.right(biomeEntries));
   }

   private MultiNoiseUtil.Entries getBiomeEntries() {
      return (MultiNoiseUtil.Entries)this.biomeEntries.map((entries) -> {
         return entries;
      }, (parameterListEntry) -> {
         return ((MultiNoiseBiomeSourceParameterList)parameterListEntry.value()).getEntries();
      });
   }

   protected Stream biomeStream() {
      return this.getBiomeEntries().getEntries().stream().map(Pair::getSecond);
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   public boolean matchesInstance(RegistryKey parameterList) {
      Optional optional = this.biomeEntries.right();
      return optional.isPresent() && ((RegistryEntry)optional.get()).matchesKey(parameterList);
   }

   public RegistryEntry getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
      return this.getBiomeAtPoint(noise.sample(x, y, z));
   }

   @Debug
   public RegistryEntry getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
      return (RegistryEntry)this.getBiomeEntries().get(point);
   }

   public void addDebugInfo(List info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
      int i = BiomeCoords.fromBlock(pos.getX());
      int j = BiomeCoords.fromBlock(pos.getY());
      int k = BiomeCoords.fromBlock(pos.getZ());
      MultiNoiseUtil.NoiseValuePoint noiseValuePoint = noiseSampler.sample(i, j, k);
      float f = MultiNoiseUtil.toFloat(noiseValuePoint.continentalnessNoise());
      float g = MultiNoiseUtil.toFloat(noiseValuePoint.erosionNoise());
      float h = MultiNoiseUtil.toFloat(noiseValuePoint.temperatureNoise());
      float l = MultiNoiseUtil.toFloat(noiseValuePoint.humidityNoise());
      float m = MultiNoiseUtil.toFloat(noiseValuePoint.weirdnessNoise());
      double d = (double)DensityFunctions.getPeaksValleysNoise(m);
      VanillaBiomeParameters vanillaBiomeParameters = new VanillaBiomeParameters();
      String var10001 = VanillaBiomeParameters.getPeaksValleysDescription(d);
      info.add("Biome builder PV: " + var10001 + " C: " + vanillaBiomeParameters.getContinentalnessDescription((double)f) + " E: " + vanillaBiomeParameters.getErosionDescription((double)g) + " T: " + vanillaBiomeParameters.getTemperatureDescription((double)h) + " H: " + vanillaBiomeParameters.getHumidityDescription((double)l));
   }

   static {
      BIOME_CODEC = Biome.REGISTRY_CODEC.fieldOf("biome");
      CUSTOM_CODEC = MultiNoiseUtil.Entries.createCodec(BIOME_CODEC).fieldOf("biomes");
      PRESET_CODEC = MultiNoiseBiomeSourceParameterList.REGISTRY_CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable());
      CODEC = Codec.mapEither(CUSTOM_CODEC, PRESET_CODEC).xmap(MultiNoiseBiomeSource::new, (source) -> {
         return source.biomeEntries;
      });
   }
}
