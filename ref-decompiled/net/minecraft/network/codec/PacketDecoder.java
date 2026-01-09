package net.minecraft.network.codec;

@FunctionalInterface
public interface PacketDecoder {
   Object decode(Object buf);
}
