package net.minecraft.loot.provider.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootNumberProviderTypes {
   private static final Codec BASE_CODEC;
   public static final Codec CODEC;
   public static final LootNumberProviderType CONSTANT;
   public static final LootNumberProviderType UNIFORM;
   public static final LootNumberProviderType BINOMIAL;
   public static final LootNumberProviderType SCORE;
   public static final LootNumberProviderType STORAGE;
   public static final LootNumberProviderType ENCHANTMENT_LEVEL;

   private static LootNumberProviderType register(String id, MapCodec codec) {
      return (LootNumberProviderType)Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, (Identifier)Identifier.ofVanilla(id), new LootNumberProviderType(codec));
   }

   static {
      BASE_CODEC = Registries.LOOT_NUMBER_PROVIDER_TYPE.getCodec().dispatch(LootNumberProvider::getType, LootNumberProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> {
         Codec codec = Codec.withAlternative(BASE_CODEC, UniformLootNumberProvider.CODEC.codec());
         return Codec.either(ConstantLootNumberProvider.INLINE_CODEC, codec).xmap(Either::unwrap, (provider) -> {
            Either var10000;
            if (provider instanceof ConstantLootNumberProvider constantLootNumberProvider) {
               var10000 = Either.left(constantLootNumberProvider);
            } else {
               var10000 = Either.right(provider);
            }

            return var10000;
         });
      });
      CONSTANT = register("constant", ConstantLootNumberProvider.CODEC);
      UNIFORM = register("uniform", UniformLootNumberProvider.CODEC);
      BINOMIAL = register("binomial", BinomialLootNumberProvider.CODEC);
      SCORE = register("score", ScoreLootNumberProvider.CODEC);
      STORAGE = register("storage", StorageLootNumberProvider.CODEC);
      ENCHANTMENT_LEVEL = register("enchantment_level", EnchantmentLevelLootNumberProvider.CODEC);
   }
}
