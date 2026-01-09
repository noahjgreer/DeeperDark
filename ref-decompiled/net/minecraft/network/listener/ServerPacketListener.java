package net.minecraft.network.listener;

import net.minecraft.network.NetworkSide;

public interface ServerPacketListener extends PacketListener {
   default NetworkSide getSide() {
      return NetworkSide.SERVERBOUND;
   }
}
