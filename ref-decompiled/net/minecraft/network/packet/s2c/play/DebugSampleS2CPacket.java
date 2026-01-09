package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.profiler.log.DebugSampleType;

public record DebugSampleS2CPacket(long[] sample, DebugSampleType debugSampleType) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(DebugSampleS2CPacket::write, DebugSampleS2CPacket::new);

   private DebugSampleS2CPacket(PacketByteBuf buf) {
      this(buf.readLongArray(), (DebugSampleType)buf.readEnumConstant(DebugSampleType.class));
   }

   public DebugSampleS2CPacket(long[] ls, DebugSampleType debugSampleType) {
      this.sample = ls;
      this.debugSampleType = debugSampleType;
   }

   private void write(PacketByteBuf buf) {
      buf.writeLongArray(this.sample);
      buf.writeEnumConstant(this.debugSampleType);
   }

   public PacketType getPacketType() {
      return PlayPackets.DEBUG_SAMPLE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onDebugSample(this);
   }

   public long[] sample() {
      return this.sample;
   }

   public DebugSampleType debugSampleType() {
      return this.debugSampleType;
   }
}
