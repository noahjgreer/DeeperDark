package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class CloseScreenS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CloseScreenS2CPacket::write, CloseScreenS2CPacket::new);
   private final int syncId;

   public CloseScreenS2CPacket(int syncId) {
      this.syncId = syncId;
   }

   private CloseScreenS2CPacket(PacketByteBuf buf) {
      this.syncId = buf.readSyncId();
   }

   private void write(PacketByteBuf buf) {
      buf.writeSyncId(this.syncId);
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_CLOSE_S2C;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onCloseScreen(this);
   }

   public int getSyncId() {
      return this.syncId;
   }
}
