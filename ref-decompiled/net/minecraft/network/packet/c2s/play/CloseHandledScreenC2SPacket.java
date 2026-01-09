package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class CloseHandledScreenC2SPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CloseHandledScreenC2SPacket::write, CloseHandledScreenC2SPacket::new);
   private final int syncId;

   public CloseHandledScreenC2SPacket(int syncId) {
      this.syncId = syncId;
   }

   private CloseHandledScreenC2SPacket(PacketByteBuf buf) {
      this.syncId = buf.readSyncId();
   }

   private void write(PacketByteBuf buf) {
      buf.writeSyncId(this.syncId);
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_CLOSE_C2S;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onCloseHandledScreen(this);
   }

   public int getSyncId() {
      return this.syncId;
   }
}
