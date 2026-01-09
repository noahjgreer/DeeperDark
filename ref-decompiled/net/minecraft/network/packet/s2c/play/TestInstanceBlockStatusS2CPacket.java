package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Vec3i;

public record TestInstanceBlockStatusS2CPacket(Text status, Optional size) implements Packet {
   public static final PacketCodec CODEC;

   public TestInstanceBlockStatusS2CPacket(Text text, Optional optional) {
      this.status = text;
      this.size = optional;
   }

   public PacketType getPacketType() {
      return PlayPackets.TEST_INSTANCE_BLOCK_STATUS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onTestInstanceBlockStatus(this);
   }

   public Text status() {
      return this.status;
   }

   public Optional size() {
      return this.size;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.REGISTRY_PACKET_CODEC, TestInstanceBlockStatusS2CPacket::status, PacketCodecs.optional(Vec3i.PACKET_CODEC), TestInstanceBlockStatusS2CPacket::size, TestInstanceBlockStatusS2CPacket::new);
   }
}
