package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerActionResponseS2CPacket(int sequence) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PlayerActionResponseS2CPacket::write, PlayerActionResponseS2CPacket::new);

   private PlayerActionResponseS2CPacket(PacketByteBuf buf) {
      this(buf.readVarInt());
   }

   public PlayerActionResponseS2CPacket(int i) {
      this.sequence = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.sequence);
   }

   public PacketType getPacketType() {
      return PlayPackets.BLOCK_CHANGED_ACK;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerActionResponse(this);
   }

   public int sequence() {
      return this.sequence;
   }
}
