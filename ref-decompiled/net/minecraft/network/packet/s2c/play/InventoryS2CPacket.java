package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record InventoryS2CPacket(int syncId, int revision, List contents, ItemStack cursorStack) implements Packet {
   public static final PacketCodec CODEC;

   public InventoryS2CPacket(int i, int j, List list, ItemStack itemStack) {
      this.syncId = i;
      this.revision = j;
      this.contents = list;
      this.cursorStack = itemStack;
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_SET_CONTENT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onInventory(this);
   }

   public int syncId() {
      return this.syncId;
   }

   public int revision() {
      return this.revision;
   }

   public List contents() {
      return this.contents;
   }

   public ItemStack cursorStack() {
      return this.cursorStack;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, InventoryS2CPacket::syncId, PacketCodecs.VAR_INT, InventoryS2CPacket::revision, ItemStack.OPTIONAL_LIST_PACKET_CODEC, InventoryS2CPacket::contents, ItemStack.OPTIONAL_PACKET_CODEC, InventoryS2CPacket::cursorStack, InventoryS2CPacket::new);
   }
}
