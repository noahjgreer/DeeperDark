package net.minecraft.loot.provider.score;

import com.mojang.serialization.MapCodec;

public record LootScoreProviderType(MapCodec codec) {
   public LootScoreProviderType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
