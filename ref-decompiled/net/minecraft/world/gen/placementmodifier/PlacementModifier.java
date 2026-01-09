package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public abstract class PlacementModifier {
   public static final Codec CODEC;

   public abstract Stream getPositions(FeaturePlacementContext context, Random random, BlockPos pos);

   public abstract PlacementModifierType getType();

   static {
      CODEC = Registries.PLACEMENT_MODIFIER_TYPE.getCodec().dispatch(PlacementModifier::getType, PlacementModifierType::codec);
   }
}
