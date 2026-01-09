package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.NetworkRecipeId;

public record CraftRequestC2SPacket(int syncId, NetworkRecipeId recipeId, boolean craftAll) implements Packet {
   public static final PacketCodec CODEC;

   public CraftRequestC2SPacket(int i, NetworkRecipeId networkRecipeId, boolean bl) {
      this.syncId = i;
      this.recipeId = networkRecipeId;
      this.craftAll = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.PLACE_RECIPE;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onCraftRequest(this);
   }

   public int syncId() {
      return this.syncId;
   }

   public NetworkRecipeId recipeId() {
      return this.recipeId;
   }

   public boolean craftAll() {
      return this.craftAll;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, CraftRequestC2SPacket::syncId, NetworkRecipeId.PACKET_CODEC, CraftRequestC2SPacket::recipeId, PacketCodecs.BOOLEAN, CraftRequestC2SPacket::craftAll, CraftRequestC2SPacket::new);
   }
}
