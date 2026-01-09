package net.minecraft.network.listener;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerCrashSafePacketListener {
   default NetworkPhase getPhase() {
      return NetworkPhase.HANDSHAKING;
   }

   void onHandshake(HandshakeC2SPacket packet);
}
