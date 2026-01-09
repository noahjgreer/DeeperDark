package net.minecraft.network.packet.s2c.common;

import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.entry.RegistryEntry;

public record ShowDialogS2CPacket(RegistryEntry dialog) implements Packet {
   public static final PacketCodec REGISTRY_CODEC;
   public static final PacketCodec CODEC;

   public ShowDialogS2CPacket(RegistryEntry registryEntry) {
      this.dialog = registryEntry;
   }

   public PacketType getPacketType() {
      return CommonPackets.SHOW_DIALOG;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onShowDialog(this);
   }

   public RegistryEntry dialog() {
      return this.dialog;
   }

   static {
      REGISTRY_CODEC = PacketCodec.tuple(Dialog.ENTRY_PACKET_CODEC, ShowDialogS2CPacket::dialog, ShowDialogS2CPacket::new);
      CODEC = PacketCodec.tuple(Dialog.PACKET_CODEC.xmap(RegistryEntry::of, RegistryEntry::value), ShowDialogS2CPacket::dialog, ShowDialogS2CPacket::new);
   }
}
