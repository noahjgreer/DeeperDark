package net.minecraft.network.packet.c2s.play;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record BookUpdateC2SPacket(int slot, List pages, Optional title) implements Packet {
   public static final PacketCodec CODEC;

   public BookUpdateC2SPacket(int slot, List pages, Optional title) {
      pages = List.copyOf(pages);
      this.slot = slot;
      this.pages = pages;
      this.title = title;
   }

   public PacketType getPacketType() {
      return PlayPackets.EDIT_BOOK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onBookUpdate(this);
   }

   public int slot() {
      return this.slot;
   }

   public List pages() {
      return this.pages;
   }

   public Optional title() {
      return this.title;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, BookUpdateC2SPacket::slot, PacketCodecs.string(1024).collect(PacketCodecs.toList(100)), BookUpdateC2SPacket::pages, PacketCodecs.string(32).collect(PacketCodecs::optional), BookUpdateC2SPacket::title, BookUpdateC2SPacket::new);
   }
}
