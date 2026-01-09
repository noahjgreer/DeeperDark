package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record UpdateBeaconC2SPacket(Optional primary, Optional secondary) implements Packet {
   public static final PacketCodec CODEC;

   public UpdateBeaconC2SPacket(Optional primaryEffectId, Optional secondaryEffectId) {
      this.primary = primaryEffectId;
      this.secondary = secondaryEffectId;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_BEACON;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onUpdateBeacon(this);
   }

   public Optional primary() {
      return this.primary;
   }

   public Optional secondary() {
      return this.secondary;
   }

   static {
      CODEC = PacketCodec.tuple(StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), UpdateBeaconC2SPacket::primary, StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), UpdateBeaconC2SPacket::secondary, UpdateBeaconC2SPacket::new);
   }
}
