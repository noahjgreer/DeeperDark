package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public class HandshakeStates {
   public static final NetworkStateFactory C2S_FACTORY;
   public static final NetworkState C2S;

   static {
      C2S_FACTORY = NetworkStateBuilder.c2s(NetworkPhase.HANDSHAKING, (builder) -> {
         builder.add(HandshakePackets.INTENTION, HandshakeC2SPacket.CODEC);
      });
      C2S = C2S_FACTORY.bind(PacketByteBuf::new);
   }
}
