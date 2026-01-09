package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface HeightProviderType {
   HeightProviderType CONSTANT = register("constant", ConstantHeightProvider.CONSTANT_CODEC);
   HeightProviderType UNIFORM = register("uniform", UniformHeightProvider.UNIFORM_CODEC);
   HeightProviderType BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomHeightProvider.BIASED_TO_BOTTOM_CODEC);
   HeightProviderType VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", VeryBiasedToBottomHeightProvider.CODEC);
   HeightProviderType TRAPEZOID = register("trapezoid", TrapezoidHeightProvider.CODEC);
   HeightProviderType WEIGHTED_LIST = register("weighted_list", WeightedListHeightProvider.WEIGHTED_LIST_CODEC);

   MapCodec codec();

   private static HeightProviderType register(String id, MapCodec codec) {
      return (HeightProviderType)Registry.register(Registries.HEIGHT_PROVIDER_TYPE, (String)id, () -> {
         return codec;
      });
   }
}
