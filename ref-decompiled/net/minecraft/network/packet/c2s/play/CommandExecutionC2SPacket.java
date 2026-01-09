package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record CommandExecutionC2SPacket(String command) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CommandExecutionC2SPacket::write, CommandExecutionC2SPacket::new);

   private CommandExecutionC2SPacket(PacketByteBuf buf) {
      this(buf.readString());
   }

   public CommandExecutionC2SPacket(String string) {
      this.command = string;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.command);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHAT_COMMAND;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onCommandExecution(this);
   }

   public String command() {
      return this.command;
   }
}
