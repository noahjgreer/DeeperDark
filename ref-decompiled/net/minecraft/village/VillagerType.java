package net.minecraft.village;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.BiomeKeys;

public final class VillagerType {
   public static final RegistryKey DESERT = of("desert");
   public static final RegistryKey JUNGLE = of("jungle");
   public static final RegistryKey PLAINS = of("plains");
   public static final RegistryKey SAVANNA = of("savanna");
   public static final RegistryKey SNOW = of("snow");
   public static final RegistryKey SWAMP = of("swamp");
   public static final RegistryKey TAIGA = of("taiga");
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final Map BIOME_TO_TYPE;

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.VILLAGER_TYPE, Identifier.ofVanilla(id));
   }

   private static VillagerType create(Registry registry, RegistryKey key) {
      return (VillagerType)Registry.register(registry, (RegistryKey)key, new VillagerType());
   }

   public static VillagerType registerAndGetDefault(Registry registry) {
      create(registry, DESERT);
      create(registry, JUNGLE);
      create(registry, PLAINS);
      create(registry, SAVANNA);
      create(registry, SNOW);
      create(registry, SWAMP);
      return create(registry, TAIGA);
   }

   public static RegistryKey forBiome(RegistryEntry biomeEntry) {
      Optional var10000 = biomeEntry.getKey();
      Map var10001 = BIOME_TO_TYPE;
      Objects.requireNonNull(var10001);
      return (RegistryKey)var10000.map(var10001::get).orElse(PLAINS);
   }

   static {
      CODEC = RegistryFixedCodec.of(RegistryKeys.VILLAGER_TYPE);
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.VILLAGER_TYPE);
      BIOME_TO_TYPE = (Map)Util.make(Maps.newHashMap(), (map) -> {
         map.put(BiomeKeys.BADLANDS, DESERT);
         map.put(BiomeKeys.DESERT, DESERT);
         map.put(BiomeKeys.ERODED_BADLANDS, DESERT);
         map.put(BiomeKeys.WOODED_BADLANDS, DESERT);
         map.put(BiomeKeys.BAMBOO_JUNGLE, JUNGLE);
         map.put(BiomeKeys.JUNGLE, JUNGLE);
         map.put(BiomeKeys.SPARSE_JUNGLE, JUNGLE);
         map.put(BiomeKeys.SAVANNA_PLATEAU, SAVANNA);
         map.put(BiomeKeys.SAVANNA, SAVANNA);
         map.put(BiomeKeys.WINDSWEPT_SAVANNA, SAVANNA);
         map.put(BiomeKeys.DEEP_FROZEN_OCEAN, SNOW);
         map.put(BiomeKeys.FROZEN_OCEAN, SNOW);
         map.put(BiomeKeys.FROZEN_RIVER, SNOW);
         map.put(BiomeKeys.ICE_SPIKES, SNOW);
         map.put(BiomeKeys.SNOWY_BEACH, SNOW);
         map.put(BiomeKeys.SNOWY_TAIGA, SNOW);
         map.put(BiomeKeys.SNOWY_PLAINS, SNOW);
         map.put(BiomeKeys.GROVE, SNOW);
         map.put(BiomeKeys.SNOWY_SLOPES, SNOW);
         map.put(BiomeKeys.FROZEN_PEAKS, SNOW);
         map.put(BiomeKeys.JAGGED_PEAKS, SNOW);
         map.put(BiomeKeys.SWAMP, SWAMP);
         map.put(BiomeKeys.MANGROVE_SWAMP, SWAMP);
         map.put(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, TAIGA);
         map.put(BiomeKeys.OLD_GROWTH_PINE_TAIGA, TAIGA);
         map.put(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, TAIGA);
         map.put(BiomeKeys.WINDSWEPT_HILLS, TAIGA);
         map.put(BiomeKeys.TAIGA, TAIGA);
         map.put(BiomeKeys.WINDSWEPT_FOREST, TAIGA);
      });
   }
}
