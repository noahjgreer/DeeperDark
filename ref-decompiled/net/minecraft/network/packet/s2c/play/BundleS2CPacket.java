package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class BundleS2CPacket extends BundlePacket {
   public BundleS2CPacket(Iterable iterable) {
      super(iterable);
   }

   public PacketType getPacketType() {
      return PlayPackets.BUNDLE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onBundle(this);
   }
}
