package net.minecraft.network.packet.c2s.common;

import java.util.Optional;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record CustomClickActionC2SPacket(Identifier id, Optional payload) implements Packet {
   private static final PacketCodec field_60958 = PacketCodecs.nbtElement(() -> {
      return new NbtSizeTracker(32768L, 16);
   }).collect(PacketCodecs.lengthPrepended(65536));
   public static final PacketCodec CODEC;

   public CustomClickActionC2SPacket(Identifier identifier, Optional optional) {
      this.id = identifier;
      this.payload = optional;
   }

   public PacketType getPacketType() {
      return CommonPackets.CUSTOM_CLICK_ACTION;
   }

   public void apply(ServerCommonPacketListener serverCommonPacketListener) {
      serverCommonPacketListener.onCustomClickAction(this);
   }

   public Identifier id() {
      return this.id;
   }

   public Optional payload() {
      return this.payload;
   }

   static {
      CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, CustomClickActionC2SPacket::id, field_60958, CustomClickActionC2SPacket::payload, CustomClickActionC2SPacket::new);
   }
}
