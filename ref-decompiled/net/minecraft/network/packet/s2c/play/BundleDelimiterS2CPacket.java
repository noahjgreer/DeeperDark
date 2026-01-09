package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class BundleDelimiterS2CPacket extends BundleSplitterPacket {
   public PacketType getPacketType() {
      return PlayPackets.BUNDLE_DELIMITER;
   }
}
