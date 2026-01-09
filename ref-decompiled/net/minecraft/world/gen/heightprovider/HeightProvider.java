package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public abstract class HeightProvider {
   private static final Codec OFFSET_OR_HEIGHT_CODEC;
   public static final Codec CODEC;

   public abstract int get(Random random, HeightContext context);

   public abstract HeightProviderType getType();

   static {
      OFFSET_OR_HEIGHT_CODEC = Codec.either(YOffset.OFFSET_CODEC, Registries.HEIGHT_PROVIDER_TYPE.getCodec().dispatch(HeightProvider::getType, HeightProviderType::codec));
      CODEC = OFFSET_OR_HEIGHT_CODEC.xmap((either) -> {
         return (HeightProvider)either.map(ConstantHeightProvider::create, (provider) -> {
            return provider;
         });
      }, (provider) -> {
         return provider.getType() == HeightProviderType.CONSTANT ? Either.left(((ConstantHeightProvider)provider).getOffset()) : Either.right(provider);
      });
   }
}
