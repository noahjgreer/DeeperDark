package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public record PickItemFromBlockC2SPacket(BlockPos pos, boolean includeData) implements Packet {
   public static final PacketCodec CODEC;

   public PickItemFromBlockC2SPacket(BlockPos blockPos, boolean bl) {
      this.pos = blockPos;
      this.includeData = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.PICK_ITEM_FROM_BLOCK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPickItemFromBlock(this);
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean includeData() {
      return this.includeData;
   }

   static {
      CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, PickItemFromBlockC2SPacket::pos, PacketCodecs.BOOLEAN, PickItemFromBlockC2SPacket::includeData, PickItemFromBlockC2SPacket::new);
   }
}
