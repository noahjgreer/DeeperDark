package net.minecraft.network.packet.c2s.config;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.VersionedIdentifier;

public record SelectKnownPacksC2SPacket(List knownPacks) implements Packet {
   public static final PacketCodec CODEC;

   public SelectKnownPacksC2SPacket(List list) {
      this.knownPacks = list;
   }

   public PacketType getPacketType() {
      return ConfigPackets.SELECT_KNOWN_PACKS_C2S;
   }

   public void apply(ServerConfigurationPacketListener serverConfigurationPacketListener) {
      serverConfigurationPacketListener.onSelectKnownPacks(this);
   }

   public List knownPacks() {
      return this.knownPacks;
   }

   static {
      CODEC = PacketCodec.tuple(VersionedIdentifier.PACKET_CODEC.collect(PacketCodecs.toList(64)), SelectKnownPacksC2SPacket::knownPacks, SelectKnownPacksC2SPacket::new);
   }
}
