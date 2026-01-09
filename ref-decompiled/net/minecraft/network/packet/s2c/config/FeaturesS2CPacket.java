package net.minecraft.network.packet.s2c.config;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record FeaturesS2CPacket(Set features) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(FeaturesS2CPacket::write, FeaturesS2CPacket::new);

   private FeaturesS2CPacket(PacketByteBuf buf) {
      this((Set)buf.readCollection(HashSet::new, PacketByteBuf::readIdentifier));
   }

   public FeaturesS2CPacket(Set set) {
      this.features = set;
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.features, PacketByteBuf::writeIdentifier);
   }

   public PacketType getPacketType() {
      return ConfigPackets.UPDATE_ENABLED_FEATURES;
   }

   public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
      clientConfigurationPacketListener.onFeatures(this);
   }

   public Set features() {
      return this.features;
   }
}
