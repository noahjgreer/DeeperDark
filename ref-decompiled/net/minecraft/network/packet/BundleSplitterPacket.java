package net.minecraft.network.packet;

import net.minecraft.network.listener.PacketListener;

public abstract class BundleSplitterPacket implements Packet {
   public final void apply(PacketListener listener) {
      throw new AssertionError("This packet should be handled by pipeline");
   }

   public abstract PacketType getPacketType();
}
