package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.NetworkRecipeId;

public record RecipeBookDataC2SPacket(NetworkRecipeId recipeId) implements Packet {
   public static final PacketCodec CODEC;

   public RecipeBookDataC2SPacket(NetworkRecipeId networkRecipeId) {
      this.recipeId = networkRecipeId;
   }

   public PacketType getPacketType() {
      return PlayPackets.RECIPE_BOOK_SEEN_RECIPE;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onRecipeBookData(this);
   }

   public NetworkRecipeId recipeId() {
      return this.recipeId;
   }

   static {
      CODEC = PacketCodec.tuple(NetworkRecipeId.PACKET_CODEC, RecipeBookDataC2SPacket::recipeId, RecipeBookDataC2SPacket::new);
   }
}
