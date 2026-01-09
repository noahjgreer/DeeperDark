package net.minecraft.recipe;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record NetworkRecipeId(int index) {
   public static final PacketCodec PACKET_CODEC;

   public NetworkRecipeId(int i) {
      this.index = i;
   }

   public int index() {
      return this.index;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, NetworkRecipeId::index, NetworkRecipeId::new);
   }
}
