package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SimulationDistanceS2CPacket(int simulationDistance) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(SimulationDistanceS2CPacket::write, SimulationDistanceS2CPacket::new);

   private SimulationDistanceS2CPacket(PacketByteBuf buf) {
      this(buf.readVarInt());
   }

   public SimulationDistanceS2CPacket(int i) {
      this.simulationDistance = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.simulationDistance);
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_SIMULATION_DISTANCE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onSimulationDistance(this);
   }

   public int simulationDistance() {
      return this.simulationDistance;
   }
}
