package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record UpdateSelectedSlotS2CPacket(int slot) implements Packet {
   public static final PacketCodec CODEC;

   public UpdateSelectedSlotS2CPacket(int i) {
      this.slot = i;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_CARRIED_ITEM_S2C;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onUpdateSelectedSlot(this);
   }

   public int slot() {
      return this.slot;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, UpdateSelectedSlotS2CPacket::slot, UpdateSelectedSlotS2CPacket::new);
   }
}
