package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface FloatProviderType {
   FloatProviderType CONSTANT = register("constant", ConstantFloatProvider.CODEC);
   FloatProviderType UNIFORM = register("uniform", UniformFloatProvider.CODEC);
   FloatProviderType CLAMPED_NORMAL = register("clamped_normal", ClampedNormalFloatProvider.CODEC);
   FloatProviderType TRAPEZOID = register("trapezoid", TrapezoidFloatProvider.CODEC);

   MapCodec codec();

   static FloatProviderType register(String id, MapCodec codec) {
      return (FloatProviderType)Registry.register(Registries.FLOAT_PROVIDER_TYPE, (String)id, () -> {
         return codec;
      });
   }
}
