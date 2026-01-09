package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;

public record LootConditionType(MapCodec codec) {
   public LootConditionType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
