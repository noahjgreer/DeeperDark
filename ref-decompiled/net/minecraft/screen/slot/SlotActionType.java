package net.minecraft.screen.slot;

import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public enum SlotActionType {
   PICKUP(0),
   QUICK_MOVE(1),
   SWAP(2),
   CLONE(3),
   THROW(4),
   QUICK_CRAFT(5),
   PICKUP_ALL(6);

   private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(SlotActionType::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, SlotActionType::getIndex);
   private final int index;

   private SlotActionType(final int index) {
      this.index = index;
   }

   public int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static SlotActionType[] method_36673() {
      return new SlotActionType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
   }
}
