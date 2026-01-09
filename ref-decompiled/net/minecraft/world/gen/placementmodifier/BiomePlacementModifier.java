package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BiomePlacementModifier extends AbstractConditionalPlacementModifier {
   private static final BiomePlacementModifier INSTANCE = new BiomePlacementModifier();
   public static MapCodec MODIFIER_CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });

   private BiomePlacementModifier() {
   }

   public static BiomePlacementModifier of() {
      return INSTANCE;
   }

   protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
      PlacedFeature placedFeature = (PlacedFeature)context.getPlacedFeature().orElseThrow(() -> {
         return new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome");
      });
      RegistryEntry registryEntry = context.getWorld().getBiome(pos);
      return context.getChunkGenerator().getGenerationSettings(registryEntry).isFeatureAllowed(placedFeature);
   }

   public PlacementModifierType getType() {
      return PlacementModifierType.BIOME;
   }
}
