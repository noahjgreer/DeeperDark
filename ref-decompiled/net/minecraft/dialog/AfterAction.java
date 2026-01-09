package net.minecraft.dialog;

import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum AfterAction implements StringIdentifiable {
   CLOSE(0, "close"),
   NONE(1, "none"),
   WAIT_FOR_RESPONSE(2, "wait_for_response");

   public static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((afterAction) -> {
      return afterAction.index;
   }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(AfterAction::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, (afterAction) -> {
      return afterAction.index;
   });
   private final int index;
   private final String id;

   private AfterAction(final int index, final String id) {
      this.index = index;
      this.id = id;
   }

   public String asString() {
      return this.id;
   }

   public boolean canUnpause() {
      return this == CLOSE || this == WAIT_FOR_RESPONSE;
   }

   // $FF: synthetic method
   private static AfterAction[] method_72065() {
      return new AfterAction[]{CLOSE, NONE, WAIT_FOR_RESPONSE};
   }
}
