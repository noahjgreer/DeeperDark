package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class MultifaceGrowthFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Registries.BLOCK.getCodec().fieldOf("block").flatXmap(MultifaceGrowthFeatureConfig::validateBlock, DataResult::success).orElse((MultifaceGrowthBlock)Blocks.GLOW_LICHEN).forGetter((config) -> {
         return config.block;
      }), Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((config) -> {
         return config.searchRange;
      }), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((config) -> {
         return config.placeOnFloor;
      }), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((config) -> {
         return config.placeOnCeiling;
      }), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((config) -> {
         return config.placeOnWalls;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((config) -> {
         return config.spreadChance;
      }), RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("can_be_placed_on").forGetter((config) -> {
         return config.canPlaceOn;
      })).apply(instance, MultifaceGrowthFeatureConfig::new);
   });
   public final MultifaceGrowthBlock block;
   public final int searchRange;
   public final boolean placeOnFloor;
   public final boolean placeOnCeiling;
   public final boolean placeOnWalls;
   public final float spreadChance;
   public final RegistryEntryList canPlaceOn;
   private final ObjectArrayList directions;

   private static DataResult validateBlock(Block block) {
      DataResult var10000;
      if (block instanceof MultifaceGrowthBlock multifaceGrowthBlock) {
         var10000 = DataResult.success(multifaceGrowthBlock);
      } else {
         var10000 = DataResult.error(() -> {
            return "Growth block should be a multiface spreadeable block";
         });
      }

      return var10000;
   }

   public MultifaceGrowthFeatureConfig(MultifaceGrowthBlock block, int searchRange, boolean placeOnFloor, boolean placeOnCeiling, boolean placeOnWalls, float spreadChance, RegistryEntryList canPlaceOn) {
      this.block = block;
      this.searchRange = searchRange;
      this.placeOnFloor = placeOnFloor;
      this.placeOnCeiling = placeOnCeiling;
      this.placeOnWalls = placeOnWalls;
      this.spreadChance = spreadChance;
      this.canPlaceOn = canPlaceOn;
      this.directions = new ObjectArrayList(6);
      if (placeOnCeiling) {
         this.directions.add(Direction.UP);
      }

      if (placeOnFloor) {
         this.directions.add(Direction.DOWN);
      }

      if (placeOnWalls) {
         Direction.Type var10000 = Direction.Type.HORIZONTAL;
         ObjectArrayList var10001 = this.directions;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

   }

   public List shuffleDirections(Random random, Direction excluded) {
      return Util.copyShuffled(this.directions.stream().filter((direction) -> {
         return direction != excluded;
      }), random);
   }

   public List shuffleDirections(Random random) {
      return Util.copyShuffled(this.directions, random);
   }
}
