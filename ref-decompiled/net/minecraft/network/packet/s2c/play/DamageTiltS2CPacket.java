package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record DamageTiltS2CPacket(int id, float yaw) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(DamageTiltS2CPacket::write, DamageTiltS2CPacket::new);

   public DamageTiltS2CPacket(LivingEntity entity) {
      this(entity.getId(), entity.getDamageTiltYaw());
   }

   private DamageTiltS2CPacket(PacketByteBuf buf) {
      this(buf.readVarInt(), buf.readFloat());
   }

   public DamageTiltS2CPacket(int i, float f) {
      this.id = i;
      this.yaw = f;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.id);
      buf.writeFloat(this.yaw);
   }

   public PacketType getPacketType() {
      return PlayPackets.HURT_ANIMATION;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onDamageTilt(this);
   }

   public int id() {
      return this.id;
   }

   public float yaw() {
      return this.yaw;
   }
}
