package net.minecraft.network.listener;

import net.minecraft.network.NetworkSide;

public interface ClientPacketListener extends PacketListener {
   default NetworkSide getSide() {
      return NetworkSide.CLIENTBOUND;
   }
}
