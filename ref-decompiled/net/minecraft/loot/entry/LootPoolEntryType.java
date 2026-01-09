package net.minecraft.loot.entry;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryType(MapCodec codec) {
   public LootPoolEntryType(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
