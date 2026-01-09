package net.minecraft.network.packet.s2c.config;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.VersionedIdentifier;

public record SelectKnownPacksS2CPacket(List knownPacks) implements Packet {
   public static final PacketCodec CODEC;

   public SelectKnownPacksS2CPacket(List list) {
      this.knownPacks = list;
   }

   public PacketType getPacketType() {
      return ConfigPackets.SELECT_KNOWN_PACKS_S2C;
   }

   public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
      clientConfigurationPacketListener.onSelectKnownPacks(this);
   }

   public List knownPacks() {
      return this.knownPacks;
   }

   static {
      CODEC = PacketCodec.tuple(VersionedIdentifier.PACKET_CODEC.collect(PacketCodecs.toList()), SelectKnownPacksS2CPacket::knownPacks, SelectKnownPacksS2CPacket::new);
   }
}
