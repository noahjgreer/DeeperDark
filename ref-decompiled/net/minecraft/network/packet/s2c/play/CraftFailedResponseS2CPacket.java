package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.display.RecipeDisplay;

public record CraftFailedResponseS2CPacket(int syncId, RecipeDisplay recipeDisplay) implements Packet {
   public static final PacketCodec CODEC;

   public CraftFailedResponseS2CPacket(int i, RecipeDisplay recipeDisplay) {
      this.syncId = i;
      this.recipeDisplay = recipeDisplay;
   }

   public PacketType getPacketType() {
      return PlayPackets.PLACE_GHOST_RECIPE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onCraftFailedResponse(this);
   }

   public int syncId() {
      return this.syncId;
   }

   public RecipeDisplay recipeDisplay() {
      return this.recipeDisplay;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, CraftFailedResponseS2CPacket::syncId, RecipeDisplay.PACKET_CODEC, CraftFailedResponseS2CPacket::recipeDisplay, CraftFailedResponseS2CPacket::new);
   }
}
