package net.minecraft.network.packet.s2c.config;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.util.Identifier;

public record DynamicRegistriesS2CPacket(RegistryKey registry, List entries) implements Packet {
   private static final PacketCodec REGISTRY_KEY_CODEC;
   public static final PacketCodec CODEC;

   public DynamicRegistriesS2CPacket(RegistryKey registryKey, List list) {
      this.registry = registryKey;
      this.entries = list;
   }

   public PacketType getPacketType() {
      return ConfigPackets.REGISTRY_DATA;
   }

   public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
      clientConfigurationPacketListener.onDynamicRegistries(this);
   }

   public RegistryKey registry() {
      return this.registry;
   }

   public List entries() {
      return this.entries;
   }

   static {
      REGISTRY_KEY_CODEC = Identifier.PACKET_CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
      CODEC = PacketCodec.tuple(REGISTRY_KEY_CODEC, DynamicRegistriesS2CPacket::registry, SerializableRegistries.SerializedRegistryEntry.PACKET_CODEC.collect(PacketCodecs.toList()), DynamicRegistriesS2CPacket::entries, DynamicRegistriesS2CPacket::new);
   }
}
