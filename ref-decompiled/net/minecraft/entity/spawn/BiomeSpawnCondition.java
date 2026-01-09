package net.minecraft.entity.spawn;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record BiomeSpawnCondition(RegistryEntryList requiredBiomes) implements SpawnCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(BiomeSpawnCondition::requiredBiomes)).apply(instance, BiomeSpawnCondition::new);
   });

   public BiomeSpawnCondition(RegistryEntryList registryEntryList) {
      this.requiredBiomes = registryEntryList;
   }

   public boolean test(SpawnContext spawnContext) {
      return this.requiredBiomes.contains(spawnContext.biome());
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntryList requiredBiomes() {
      return this.requiredBiomes;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((SpawnContext)context);
   }
}
