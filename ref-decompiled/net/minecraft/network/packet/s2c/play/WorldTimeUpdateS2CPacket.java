package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record WorldTimeUpdateS2CPacket(long time, long timeOfDay, boolean tickDayTime) implements Packet {
   public static final PacketCodec CODEC;

   public WorldTimeUpdateS2CPacket(long l, long m, boolean bl) {
      this.time = l;
      this.timeOfDay = m;
      this.tickDayTime = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_TIME;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onWorldTimeUpdate(this);
   }

   public long time() {
      return this.time;
   }

   public long timeOfDay() {
      return this.timeOfDay;
   }

   public boolean tickDayTime() {
      return this.tickDayTime;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.LONG, WorldTimeUpdateS2CPacket::time, PacketCodecs.LONG, WorldTimeUpdateS2CPacket::timeOfDay, PacketCodecs.BOOLEAN, WorldTimeUpdateS2CPacket::tickDayTime, WorldTimeUpdateS2CPacket::new);
   }
}
