package net.minecraft.network.codec;

@FunctionalInterface
public interface PacketEncoder {
   void encode(Object buf, Object value);
}
