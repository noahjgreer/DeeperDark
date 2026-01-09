package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class CheckerboardBiomeSource extends BiomeSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Biome.REGISTRY_ENTRY_LIST_CODEC.fieldOf("biomes").forGetter((biomeSource) -> {
         return biomeSource.biomeArray;
      }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((biomeSource) -> {
         return biomeSource.scale;
      })).apply(instance, CheckerboardBiomeSource::new);
   });
   private final RegistryEntryList biomeArray;
   private final int gridSize;
   private final int scale;

   public CheckerboardBiomeSource(RegistryEntryList biomes, int size) {
      this.biomeArray = biomes;
      this.gridSize = size + 2;
      this.scale = size;
   }

   protected Stream biomeStream() {
      return this.biomeArray.stream();
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntry getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
      return this.biomeArray.get(Math.floorMod((x >> this.gridSize) + (z >> this.gridSize), this.biomeArray.size()));
   }
}
