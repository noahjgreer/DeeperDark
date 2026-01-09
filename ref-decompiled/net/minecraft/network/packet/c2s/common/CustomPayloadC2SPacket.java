package net.minecraft.network.packet.c2s.common;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.util.Util;

public record CustomPayloadC2SPacket(CustomPayload payload) implements Packet {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final PacketCodec CODEC;

   public CustomPayloadC2SPacket(CustomPayload customPayload) {
      this.payload = customPayload;
   }

   public PacketType getPacketType() {
      return CommonPackets.CUSTOM_PAYLOAD_C2S;
   }

   public void apply(ServerCommonPacketListener serverCommonPacketListener) {
      serverCommonPacketListener.onCustomPayload(this);
   }

   public CustomPayload payload() {
      return this.payload;
   }

   static {
      CODEC = CustomPayload.createCodec((id) -> {
         return UnknownCustomPayload.createCodec(id, 32767);
      }, (List)Util.make(Lists.newArrayList(new CustomPayload.Type[]{new CustomPayload.Type(BrandCustomPayload.ID, BrandCustomPayload.CODEC)}), (types) -> {
      })).xmap(CustomPayloadC2SPacket::new, CustomPayloadC2SPacket::payload);
   }
}
