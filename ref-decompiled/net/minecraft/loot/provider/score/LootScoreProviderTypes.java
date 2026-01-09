package net.minecraft.loot.provider.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootScoreProviderTypes {
   private static final Codec BASE_CODEC;
   public static final Codec CODEC;
   public static final LootScoreProviderType FIXED;
   public static final LootScoreProviderType CONTEXT;

   private static LootScoreProviderType register(String id, MapCodec codec) {
      return (LootScoreProviderType)Registry.register(Registries.LOOT_SCORE_PROVIDER_TYPE, (Identifier)Identifier.ofVanilla(id), new LootScoreProviderType(codec));
   }

   static {
      BASE_CODEC = Registries.LOOT_SCORE_PROVIDER_TYPE.getCodec().dispatch(LootScoreProvider::getType, LootScoreProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> {
         return Codec.either(ContextLootScoreProvider.INLINE_CODEC, BASE_CODEC).xmap(Either::unwrap, (provider) -> {
            Either var10000;
            if (provider instanceof ContextLootScoreProvider contextLootScoreProvider) {
               var10000 = Either.left(contextLootScoreProvider);
            } else {
               var10000 = Either.right(provider);
            }

            return var10000;
         });
      });
      FIXED = register("fixed", FixedLootScoreProvider.CODEC);
      CONTEXT = register("context", ContextLootScoreProvider.CODEC);
   }
}
