package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.jetbrains.annotations.Nullable;

public class FixedBiomeSource extends BiomeSource implements BiomeAccess.Storage {
   public static final MapCodec CODEC;
   private final RegistryEntry biome;

   public FixedBiomeSource(RegistryEntry biome) {
      this.biome = biome;
   }

   protected Stream biomeStream() {
      return Stream.of(this.biome);
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntry getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
      return this.biome;
   }

   public RegistryEntry getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      return this.biome;
   }

   @Nullable
   public Pair locateBiome(int x, int y, int z, int radius, int blockCheckInterval, Predicate predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
      if (predicate.test(this.biome)) {
         return bl ? Pair.of(new BlockPos(x, y, z), this.biome) : Pair.of(new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   @Nullable
   public Pair locateBiome(BlockPos origin, int radius, int horizontalBlockCheckInterval, int verticalBlockCheckInterval, Predicate predicate, MultiNoiseUtil.MultiNoiseSampler noiseSampler, WorldView world) {
      return predicate.test(this.biome) ? Pair.of(origin, this.biome) : null;
   }

   public Set getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler sampler) {
      return Sets.newHashSet(Set.of(this.biome));
   }

   static {
      CODEC = Biome.REGISTRY_CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (biomeSource) -> {
         return biomeSource.biome;
      }).stable();
   }
}
