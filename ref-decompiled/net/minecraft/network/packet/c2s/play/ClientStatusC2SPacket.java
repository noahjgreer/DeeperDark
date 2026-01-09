package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ClientStatusC2SPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ClientStatusC2SPacket::write, ClientStatusC2SPacket::new);
   private final Mode mode;

   public ClientStatusC2SPacket(Mode mode) {
      this.mode = mode;
   }

   private ClientStatusC2SPacket(PacketByteBuf buf) {
      this.mode = (Mode)buf.readEnumConstant(Mode.class);
   }

   private void write(PacketByteBuf buf) {
      buf.writeEnumConstant(this.mode);
   }

   public PacketType getPacketType() {
      return PlayPackets.CLIENT_COMMAND;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onClientStatus(this);
   }

   public Mode getMode() {
      return this.mode;
   }

   public static enum Mode {
      PERFORM_RESPAWN,
      REQUEST_STATS;

      // $FF: synthetic method
      private static Mode[] method_36955() {
         return new Mode[]{PERFORM_RESPAWN, REQUEST_STATS};
      }
   }
}
