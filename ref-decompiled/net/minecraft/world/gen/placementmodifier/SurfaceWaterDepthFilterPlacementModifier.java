package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class SurfaceWaterDepthFilterPlacementModifier extends AbstractConditionalPlacementModifier {
   public static final MapCodec MODIFIER_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.INT.fieldOf("max_water_depth").forGetter((placementModifier) -> {
         return placementModifier.maxWaterDepth;
      })).apply(instance, SurfaceWaterDepthFilterPlacementModifier::new);
   });
   private final int maxWaterDepth;

   private SurfaceWaterDepthFilterPlacementModifier(int maxWaterDepth) {
      this.maxWaterDepth = maxWaterDepth;
   }

   public static SurfaceWaterDepthFilterPlacementModifier of(int maxWaterDepth) {
      return new SurfaceWaterDepthFilterPlacementModifier(maxWaterDepth);
   }

   protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
      int i = context.getTopY(Heightmap.Type.OCEAN_FLOOR, pos.getX(), pos.getZ());
      int j = context.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
      return j - i <= this.maxWaterDepth;
   }

   public PlacementModifierType getType() {
      return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
   }
}
