package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record ConfiguredFeature(Feature feature, FeatureConfig config) {
   public static final Codec CODEC;
   public static final Codec REGISTRY_CODEC;
   public static final Codec LIST_CODEC;

   public ConfiguredFeature(Feature feature, FeatureConfig config) {
      this.feature = feature;
      this.config = config;
   }

   public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos origin) {
      return this.feature.generateIfValid(this.config, world, chunkGenerator, random, origin);
   }

   public Stream getDecoratedFeatures() {
      return Stream.concat(Stream.of(this), this.config.getDecoratedFeatures());
   }

   public String toString() {
      String var10000 = String.valueOf(this.feature);
      return "Configured: " + var10000 + ": " + String.valueOf(this.config);
   }

   public Feature feature() {
      return this.feature;
   }

   public FeatureConfig config() {
      return this.config;
   }

   static {
      CODEC = Registries.FEATURE.getCodec().dispatch((configuredFeature) -> {
         return configuredFeature.feature;
      }, Feature::getCodec);
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.CONFIGURED_FEATURE, CODEC);
      LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.CONFIGURED_FEATURE, CODEC);
   }
}
