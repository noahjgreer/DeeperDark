package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public class BiomeParticleConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ParticleTypes.TYPE_CODEC.fieldOf("options").forGetter((config) -> {
         return config.particle;
      }), Codec.FLOAT.fieldOf("probability").forGetter((config) -> {
         return config.probability;
      })).apply(instance, BiomeParticleConfig::new);
   });
   private final ParticleEffect particle;
   private final float probability;

   public BiomeParticleConfig(ParticleEffect particle, float probability) {
      this.particle = particle;
      this.probability = probability;
   }

   public ParticleEffect getParticle() {
      return this.particle;
   }

   public boolean shouldAddParticle(Random random) {
      return random.nextFloat() <= this.probability;
   }
}
