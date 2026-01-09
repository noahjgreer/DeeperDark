package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;

public record LootFunctionType(MapCodec codec) {
   public LootFunctionType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
