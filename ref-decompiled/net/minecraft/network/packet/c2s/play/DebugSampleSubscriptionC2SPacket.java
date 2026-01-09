package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.profiler.log.DebugSampleType;

public record DebugSampleSubscriptionC2SPacket(DebugSampleType sampleType) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(DebugSampleSubscriptionC2SPacket::write, DebugSampleSubscriptionC2SPacket::new);

   private DebugSampleSubscriptionC2SPacket(PacketByteBuf buf) {
      this((DebugSampleType)buf.readEnumConstant(DebugSampleType.class));
   }

   public DebugSampleSubscriptionC2SPacket(DebugSampleType debugSampleType) {
      this.sampleType = debugSampleType;
   }

   private void write(PacketByteBuf buf) {
      buf.writeEnumConstant(this.sampleType);
   }

   public PacketType getPacketType() {
      return PlayPackets.DEBUG_SAMPLE_SUBSCRIPTION;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onDebugSampleSubscription(this);
   }

   public DebugSampleType sampleType() {
      return this.sampleType;
   }
}
