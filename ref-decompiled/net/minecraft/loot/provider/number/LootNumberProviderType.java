package net.minecraft.loot.provider.number;

import com.mojang.serialization.MapCodec;

public record LootNumberProviderType(MapCodec codec) {
   public LootNumberProviderType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
