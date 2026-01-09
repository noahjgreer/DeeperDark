package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerRotationS2CPacket(float yRot, float xRot) implements Packet {
   public static final PacketCodec CODEC;

   public PlayerRotationS2CPacket(float f, float g) {
      this.yRot = f;
      this.xRot = g;
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_ROTATION;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerRotation(this);
   }

   public float yRot() {
      return this.yRot;
   }

   public float xRot() {
      return this.xRot;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, PlayerRotationS2CPacket::yRot, PacketCodecs.FLOAT, PlayerRotationS2CPacket::xRot, PlayerRotationS2CPacket::new);
   }
}
