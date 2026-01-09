package net.minecraft.network.handler;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class SideValidatingDispatchingCodecBuilder {
   private final PacketCodecDispatcher.Builder backingBuilder = PacketCodecDispatcher.builder(Packet::getPacketType);
   private final NetworkSide side;

   public SideValidatingDispatchingCodecBuilder(NetworkSide side) {
      this.side = side;
   }

   public SideValidatingDispatchingCodecBuilder add(PacketType id, PacketCodec codec) {
      if (id.side() != this.side) {
         String var10002 = String.valueOf(id);
         throw new IllegalArgumentException("Invalid packet flow for packet " + var10002 + ", expected " + this.side.name());
      } else {
         this.backingBuilder.add(id, codec);
         return this;
      }
   }

   public PacketCodec build() {
      return this.backingBuilder.build();
   }
}
