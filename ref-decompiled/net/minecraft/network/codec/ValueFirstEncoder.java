package net.minecraft.network.codec;

@FunctionalInterface
public interface ValueFirstEncoder {
   void encode(Object value, Object buf);
}
