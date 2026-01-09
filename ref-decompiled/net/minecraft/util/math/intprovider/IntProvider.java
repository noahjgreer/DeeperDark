package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

public abstract class IntProvider {
   private static final Codec INT_CODEC;
   public static final Codec VALUE_CODEC;
   public static final Codec NON_NEGATIVE_CODEC;
   public static final Codec POSITIVE_CODEC;

   public static Codec createValidatingCodec(int min, int max) {
      return createValidatingCodec(min, max, VALUE_CODEC);
   }

   public static Codec createValidatingCodec(int min, int max, Codec providerCodec) {
      return providerCodec.validate((provider) -> {
         return validateProvider(min, max, provider);
      });
   }

   private static DataResult validateProvider(int min, int max, IntProvider provider) {
      if (provider.getMin() < min) {
         return DataResult.error(() -> {
            return "Value provider too low: " + min + " [" + provider.getMin() + "-" + provider.getMax() + "]";
         });
      } else {
         return provider.getMax() > max ? DataResult.error(() -> {
            return "Value provider too high: " + max + " [" + provider.getMin() + "-" + provider.getMax() + "]";
         }) : DataResult.success(provider);
      }
   }

   public abstract int get(Random random);

   public abstract int getMin();

   public abstract int getMax();

   public abstract IntProviderType getType();

   static {
      INT_CODEC = Codec.either(Codec.INT, Registries.INT_PROVIDER_TYPE.getCodec().dispatch(IntProvider::getType, IntProviderType::codec));
      VALUE_CODEC = INT_CODEC.xmap((either) -> {
         return (IntProvider)either.map(ConstantIntProvider::create, (provider) -> {
            return provider;
         });
      }, (provider) -> {
         return provider.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantIntProvider)provider).getValue()) : Either.right(provider);
      });
      NON_NEGATIVE_CODEC = createValidatingCodec(0, Integer.MAX_VALUE);
      POSITIVE_CODEC = createValidatingCodec(1, Integer.MAX_VALUE);
   }
}
