package net.minecraft.network.packet;

public abstract class BundlePacket implements Packet {
   private final Iterable packets;

   protected BundlePacket(Iterable packets) {
      this.packets = packets;
   }

   public final Iterable getPackets() {
      return this.packets;
   }

   public abstract PacketType getPacketType();
}
