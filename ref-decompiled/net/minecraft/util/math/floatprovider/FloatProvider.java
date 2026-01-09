package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registries;

public abstract class FloatProvider implements FloatSupplier {
   private static final Codec FLOAT_CODEC;
   public static final Codec VALUE_CODEC;

   public static Codec createValidatedCodec(float min, float max) {
      return VALUE_CODEC.validate((provider) -> {
         if (provider.getMin() < min) {
            return DataResult.error(() -> {
               return "Value provider too low: " + min + " [" + provider.getMin() + "-" + provider.getMax() + "]";
            });
         } else {
            return provider.getMax() > max ? DataResult.error(() -> {
               return "Value provider too high: " + max + " [" + provider.getMin() + "-" + provider.getMax() + "]";
            }) : DataResult.success(provider);
         }
      });
   }

   public abstract float getMin();

   public abstract float getMax();

   public abstract FloatProviderType getType();

   static {
      FLOAT_CODEC = Codec.either(Codec.FLOAT, Registries.FLOAT_PROVIDER_TYPE.getCodec().dispatch(FloatProvider::getType, FloatProviderType::codec));
      VALUE_CODEC = FLOAT_CODEC.xmap((either) -> {
         return (FloatProvider)either.map(ConstantFloatProvider::create, (provider) -> {
            return provider;
         });
      }, (provider) -> {
         return provider.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloatProvider)provider).getValue()) : Either.right(provider);
      });
   }
}
