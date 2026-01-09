package net.minecraft.network.packet;

import net.minecraft.network.codec.PacketCodec;

@FunctionalInterface
public interface PacketCodecModifier {
   PacketCodec apply(PacketCodec packetCodec, Object context);
}
