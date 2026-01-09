package net.minecraft.network.packet.c2s.play;

import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public record SetTestBlockC2SPacket(BlockPos position, TestBlockMode mode, String message) implements Packet {
   public static final PacketCodec CODEC;

   public SetTestBlockC2SPacket(BlockPos blockPos, TestBlockMode testBlockMode, String string) {
      this.position = blockPos;
      this.mode = testBlockMode;
      this.message = string;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_TEST_BLOCK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onSetTestBlock(this);
   }

   public BlockPos position() {
      return this.position;
   }

   public TestBlockMode mode() {
      return this.mode;
   }

   public String message() {
      return this.message;
   }

   static {
      CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SetTestBlockC2SPacket::position, TestBlockMode.PACKET_CODEC, SetTestBlockC2SPacket::mode, PacketCodecs.STRING, SetTestBlockC2SPacket::message, SetTestBlockC2SPacket::new);
   }
}
