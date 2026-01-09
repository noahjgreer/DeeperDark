package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record BundleItemSelectedC2SPacket(int slotId, int selectedItemIndex) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(BundleItemSelectedC2SPacket::write, BundleItemSelectedC2SPacket::new);

   private BundleItemSelectedC2SPacket(PacketByteBuf buf) {
      this(buf.readVarInt(), buf.readVarInt());
      if (this.selectedItemIndex < 0 && this.selectedItemIndex != -1) {
         throw new IllegalArgumentException("Invalid selectedItemIndex: " + this.selectedItemIndex);
      }
   }

   public BundleItemSelectedC2SPacket(int i, int j) {
      this.slotId = i;
      this.selectedItemIndex = j;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.slotId);
      buf.writeVarInt(this.selectedItemIndex);
   }

   public PacketType getPacketType() {
      return PlayPackets.BUNDLE_ITEM_SELECTED;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onBundleItemSelected(this);
   }

   public int slotId() {
      return this.slotId;
   }

   public int selectedItemIndex() {
      return this.selectedItemIndex;
   }
}
