package net.minecraft.util.dynamic;

import com.mojang.serialization.MapCodec;

public record CodecHolder(MapCodec codec) {
   public CodecHolder(MapCodec mapCodec) {
      this.codec = mapCodec;
   }

   public static CodecHolder of(MapCodec mapCodec) {
      return new CodecHolder(mapCodec);
   }

   public MapCodec codec() {
      return this.codec;
   }
}
