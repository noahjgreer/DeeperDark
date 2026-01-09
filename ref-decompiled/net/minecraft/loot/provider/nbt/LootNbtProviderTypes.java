package net.minecraft.loot.provider.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootNbtProviderTypes {
   private static final Codec BASE_CODEC;
   public static final Codec CODEC;
   public static final LootNbtProviderType STORAGE;
   public static final LootNbtProviderType CONTEXT;

   private static LootNbtProviderType register(String id, MapCodec codec) {
      return (LootNbtProviderType)Registry.register(Registries.LOOT_NBT_PROVIDER_TYPE, (Identifier)Identifier.ofVanilla(id), new LootNbtProviderType(codec));
   }

   static {
      BASE_CODEC = Registries.LOOT_NBT_PROVIDER_TYPE.getCodec().dispatch(LootNbtProvider::getType, LootNbtProviderType::codec);
      CODEC = Codec.lazyInitialized(() -> {
         return Codec.either(ContextLootNbtProvider.INLINE_CODEC, BASE_CODEC).xmap(Either::unwrap, (provider) -> {
            Either var10000;
            if (provider instanceof ContextLootNbtProvider contextLootNbtProvider) {
               var10000 = Either.left(contextLootNbtProvider);
            } else {
               var10000 = Either.right(provider);
            }

            return var10000;
         });
      });
      STORAGE = register("storage", StorageLootNbtProvider.CODEC);
      CONTEXT = register("context", ContextLootNbtProvider.CODEC);
   }
}
