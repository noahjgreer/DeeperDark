package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PickItemFromEntityC2SPacket(int id, boolean includeData) implements Packet {
   public static final PacketCodec CODEC;

   public PickItemFromEntityC2SPacket(int i, boolean bl) {
      this.id = i;
      this.includeData = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.PICK_ITEM_FROM_ENTITY;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPickItemFromEntity(this);
   }

   public int id() {
      return this.id;
   }

   public boolean includeData() {
      return this.includeData;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, PickItemFromEntityC2SPacket::id, PacketCodecs.BOOLEAN, PickItemFromEntityC2SPacket::includeData, PickItemFromEntityC2SPacket::new);
   }
}
