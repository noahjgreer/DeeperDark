package net.minecraft.network.packet.c2s.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;

public record ClickSlotC2SPacket(int syncId, int revision, short slot, byte button, SlotActionType actionType, Int2ObjectMap modifiedStacks, ItemStackHash cursor) implements Packet {
   private static final int MAX_MODIFIED_STACKS = 128;
   private static final PacketCodec STACK_MAP_CODEC;
   public static final PacketCodec CODEC;

   public ClickSlotC2SPacket(int i, int j, short s, byte b, SlotActionType slotActionType, Int2ObjectMap int2ObjectMap, ItemStackHash itemStackHash) {
      int2ObjectMap = Int2ObjectMaps.unmodifiable(int2ObjectMap);
      this.syncId = i;
      this.revision = j;
      this.slot = s;
      this.button = b;
      this.actionType = slotActionType;
      this.modifiedStacks = int2ObjectMap;
      this.cursor = itemStackHash;
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_CLICK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onClickSlot(this);
   }

   public int syncId() {
      return this.syncId;
   }

   public int revision() {
      return this.revision;
   }

   public short slot() {
      return this.slot;
   }

   public byte button() {
      return this.button;
   }

   public SlotActionType actionType() {
      return this.actionType;
   }

   public Int2ObjectMap modifiedStacks() {
      return this.modifiedStacks;
   }

   public ItemStackHash cursor() {
      return this.cursor;
   }

   static {
      STACK_MAP_CODEC = PacketCodecs.map(Int2ObjectOpenHashMap::new, PacketCodecs.SHORT.xmap(Short::intValue, Integer::shortValue), ItemStackHash.PACKET_CODEC, 128);
      CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, ClickSlotC2SPacket::syncId, PacketCodecs.VAR_INT, ClickSlotC2SPacket::revision, PacketCodecs.SHORT, ClickSlotC2SPacket::slot, PacketCodecs.BYTE, ClickSlotC2SPacket::button, SlotActionType.PACKET_CODEC, ClickSlotC2SPacket::actionType, STACK_MAP_CODEC, ClickSlotC2SPacket::modifiedStacks, ItemStackHash.PACKET_CODEC, ClickSlotC2SPacket::cursor, ClickSlotC2SPacket::new);
   }
}
