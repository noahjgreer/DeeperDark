package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class RenameItemC2SPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(RenameItemC2SPacket::write, RenameItemC2SPacket::new);
   private final String name;

   public RenameItemC2SPacket(String name) {
      this.name = name;
   }

   private RenameItemC2SPacket(PacketByteBuf buf) {
      this.name = buf.readString();
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.name);
   }

   public PacketType getPacketType() {
      return PlayPackets.RENAME_ITEM;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
