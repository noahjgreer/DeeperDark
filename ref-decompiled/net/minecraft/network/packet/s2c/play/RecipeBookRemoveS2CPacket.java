package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.NetworkRecipeId;

public record RecipeBookRemoveS2CPacket(List recipes) implements Packet {
   public static final PacketCodec CODEC;

   public RecipeBookRemoveS2CPacket(List list) {
      this.recipes = list;
   }

   public PacketType getPacketType() {
      return PlayPackets.RECIPE_BOOK_REMOVE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onRecipeBookRemove(this);
   }

   public List recipes() {
      return this.recipes;
   }

   static {
      CODEC = PacketCodec.tuple(NetworkRecipeId.PACKET_CODEC.collect(PacketCodecs.toList()), RecipeBookRemoveS2CPacket::recipes, RecipeBookRemoveS2CPacket::new);
   }
}
