package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SetPlayerInventoryS2CPacket(int slot, ItemStack contents) implements Packet {
   public static final PacketCodec CODEC;

   public SetPlayerInventoryS2CPacket(int i, ItemStack itemStack) {
      this.slot = i;
      this.contents = itemStack;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_PLAYER_INVENTORY;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onSetPlayerInventory(this);
   }

   public int slot() {
      return this.slot;
   }

   public ItemStack contents() {
      return this.contents;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, SetPlayerInventoryS2CPacket::slot, ItemStack.OPTIONAL_PACKET_CODEC, SetPlayerInventoryS2CPacket::contents, SetPlayerInventoryS2CPacket::new);
   }
}
