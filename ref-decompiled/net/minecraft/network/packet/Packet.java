package net.minecraft.network.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.listener.PacketListener;

public interface Packet {
   PacketType getPacketType();

   void apply(PacketListener listener);

   default boolean isWritingErrorSkippable() {
      return false;
   }

   default boolean transitionsNetworkState() {
      return false;
   }

   static PacketCodec createCodec(ValueFirstEncoder encoder, PacketDecoder decoder) {
      return PacketCodec.of(encoder, decoder);
   }
}
