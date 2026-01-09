package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.MapCodec;

public record LootNbtProviderType(MapCodec codec) {
   public LootNbtProviderType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
