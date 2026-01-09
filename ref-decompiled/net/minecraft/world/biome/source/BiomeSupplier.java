package net.minecraft.world.biome.source;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public interface BiomeSupplier {
   RegistryEntry getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise);
}
