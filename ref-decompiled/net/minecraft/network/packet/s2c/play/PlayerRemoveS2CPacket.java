package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Uuids;

public record PlayerRemoveS2CPacket(List profileIds) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PlayerRemoveS2CPacket::write, PlayerRemoveS2CPacket::new);

   private PlayerRemoveS2CPacket(PacketByteBuf buf) {
      this(buf.readList(Uuids.PACKET_CODEC));
   }

   public PlayerRemoveS2CPacket(List list) {
      this.profileIds = list;
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.profileIds, Uuids.PACKET_CODEC);
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_INFO_REMOVE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerRemove(this);
   }

   public List profileIds() {
      return this.profileIds;
   }
}
