package net.minecraft.entity.passive;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;

public class SheepColors {
   private static final SpawnConfig TEMPERATE;
   private static final SpawnConfig WARM;
   private static final SpawnConfig COLD;

   private static ColorSelector createDefaultSelector(DyeColor color) {
      return createCombinedSelector(poolBuilder().add(createSingleSelector(color), 499).add(createSingleSelector(DyeColor.PINK), 1).build());
   }

   public static DyeColor select(RegistryEntry biome, Random random) {
      SpawnConfig spawnConfig = getSpawnConfig(biome);
      return spawnConfig.colors().get(random);
   }

   private static SpawnConfig getSpawnConfig(RegistryEntry biome) {
      if (biome.isIn(BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS)) {
         return WARM;
      } else {
         return biome.isIn(BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS) ? COLD : TEMPERATE;
      }
   }

   private static ColorSelector createCombinedSelector(Pool pool) {
      if (pool.isEmpty()) {
         throw new IllegalArgumentException("List must be non-empty");
      } else {
         return (random) -> {
            return ((ColorSelector)pool.get(random)).get(random);
         };
      }
   }

   private static ColorSelector createSingleSelector(DyeColor color) {
      return (random) -> {
         return color;
      };
   }

   private static Pool.Builder poolBuilder() {
      return Pool.builder();
   }

   static {
      TEMPERATE = new SpawnConfig(createCombinedSelector(poolBuilder().add(createSingleSelector(DyeColor.BLACK), 5).add(createSingleSelector(DyeColor.GRAY), 5).add(createSingleSelector(DyeColor.LIGHT_GRAY), 5).add(createSingleSelector(DyeColor.BROWN), 3).add(createDefaultSelector(DyeColor.WHITE), 82).build()));
      WARM = new SpawnConfig(createCombinedSelector(poolBuilder().add(createSingleSelector(DyeColor.GRAY), 5).add(createSingleSelector(DyeColor.LIGHT_GRAY), 5).add(createSingleSelector(DyeColor.WHITE), 5).add(createSingleSelector(DyeColor.BLACK), 3).add(createDefaultSelector(DyeColor.BROWN), 82).build()));
      COLD = new SpawnConfig(createCombinedSelector(poolBuilder().add(createSingleSelector(DyeColor.LIGHT_GRAY), 5).add(createSingleSelector(DyeColor.GRAY), 5).add(createSingleSelector(DyeColor.WHITE), 5).add(createSingleSelector(DyeColor.BROWN), 3).add(createDefaultSelector(DyeColor.BLACK), 82).build()));
   }

   @FunctionalInterface
   private interface ColorSelector {
      DyeColor get(Random random);
   }

   static record SpawnConfig(ColorSelector colors) {
      SpawnConfig(ColorSelector colorSelector) {
         this.colors = colorSelector;
      }

      public ColorSelector colors() {
         return this.colors;
      }
   }
}
