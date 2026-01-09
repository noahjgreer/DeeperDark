package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SlotChangedStateC2SPacket(int slotId, int screenHandlerId, boolean newState) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(SlotChangedStateC2SPacket::write, SlotChangedStateC2SPacket::new);

   private SlotChangedStateC2SPacket(PacketByteBuf buf) {
      this(buf.readVarInt(), buf.readSyncId(), buf.readBoolean());
   }

   public SlotChangedStateC2SPacket(int i, int j, boolean bl) {
      this.slotId = i;
      this.screenHandlerId = j;
      this.newState = bl;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.slotId);
      buf.writeSyncId(this.screenHandlerId);
      buf.writeBoolean(this.newState);
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_SLOT_STATE_CHANGED;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onSlotChangedState(this);
   }

   public int slotId() {
      return this.slotId;
   }

   public int screenHandlerId() {
      return this.screenHandlerId;
   }

   public boolean newState() {
      return this.newState;
   }
}
