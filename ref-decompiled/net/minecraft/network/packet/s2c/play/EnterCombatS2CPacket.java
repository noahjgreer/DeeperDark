package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EnterCombatS2CPacket implements Packet {
   public static final EnterCombatS2CPacket INSTANCE = new EnterCombatS2CPacket();
   public static final PacketCodec CODEC;

   private EnterCombatS2CPacket() {
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_COMBAT_ENTER;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onEnterCombat(this);
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
