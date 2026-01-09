package net.minecraft.network.packet.s2c.query;

import com.mojang.serialization.JsonOps;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.StatusPackets;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.ServerMetadata;

public record QueryResponseS2CPacket(ServerMetadata metadata) implements Packet {
   private static final RegistryOps OPS;
   public static final PacketCodec CODEC;

   public QueryResponseS2CPacket(ServerMetadata metadata) {
      this.metadata = metadata;
   }

   public PacketType getPacketType() {
      return StatusPackets.STATUS_RESPONSE;
   }

   public void apply(ClientQueryPacketListener clientQueryPacketListener) {
      clientQueryPacketListener.onResponse(this);
   }

   public ServerMetadata metadata() {
      return this.metadata;
   }

   static {
      OPS = DynamicRegistryManager.EMPTY.getOps(JsonOps.INSTANCE);
      CODEC = PacketCodec.tuple(PacketCodecs.lenientJson(32767).collect(PacketCodecs.fromCodec(OPS, ServerMetadata.CODEC)), QueryResponseS2CPacket::metadata, QueryResponseS2CPacket::new);
   }
}
