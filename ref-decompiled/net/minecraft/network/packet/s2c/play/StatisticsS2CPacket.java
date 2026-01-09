package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.stat.Stat;

public record StatisticsS2CPacket(Object2IntMap stats) implements Packet {
   private static final PacketCodec STAT_MAP_CODEC;
   public static final PacketCodec CODEC;

   public StatisticsS2CPacket(Object2IntMap object2IntMap) {
      this.stats = object2IntMap;
   }

   public PacketType getPacketType() {
      return PlayPackets.AWARD_STATS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onStatistics(this);
   }

   public Object2IntMap stats() {
      return this.stats;
   }

   static {
      STAT_MAP_CODEC = PacketCodecs.map(Object2IntOpenHashMap::new, Stat.PACKET_CODEC, PacketCodecs.VAR_INT);
      CODEC = STAT_MAP_CODEC.xmap(StatisticsS2CPacket::new, StatisticsS2CPacket::stats);
   }
}
