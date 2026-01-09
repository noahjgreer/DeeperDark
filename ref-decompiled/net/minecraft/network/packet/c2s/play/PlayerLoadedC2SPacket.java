package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerLoadedC2SPacket() implements Packet {
   public static final PacketCodec CODEC = PacketCodec.unit(new PlayerLoadedC2SPacket());

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_LOADED;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPlayerLoaded(this);
   }
}
