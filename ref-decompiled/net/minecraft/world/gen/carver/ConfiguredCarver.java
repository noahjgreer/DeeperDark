package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public record ConfiguredCarver(Carver carver, CarverConfig config) {
   public static final Codec CODEC;
   public static final Codec REGISTRY_CODEC;
   public static final Codec LIST_CODEC;

   public ConfiguredCarver(Carver carver, CarverConfig config) {
      this.carver = carver;
      this.config = config;
   }

   public boolean shouldCarve(Random random) {
      return this.carver.shouldCarve(this.config, random);
   }

   public boolean carve(CarverContext context, Chunk chunk, Function posToBiome, Random random, AquiferSampler aquiferSampler, ChunkPos pos, CarvingMask mask) {
      return SharedConstants.isOutsideGenerationArea(chunk.getPos()) ? false : this.carver.carve(context, this.config, chunk, posToBiome, random, aquiferSampler, pos, mask);
   }

   public Carver carver() {
      return this.carver;
   }

   public CarverConfig config() {
      return this.config;
   }

   static {
      CODEC = Registries.CARVER.getCodec().dispatch((configuredCarver) -> {
         return configuredCarver.carver;
      }, Carver::getCodec);
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.CONFIGURED_CARVER, CODEC);
      LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.CONFIGURED_CARVER, CODEC);
   }
}
