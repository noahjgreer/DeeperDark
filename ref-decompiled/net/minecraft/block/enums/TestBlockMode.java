package net.minecraft.block.enums;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum TestBlockMode implements StringIdentifiable {
   START(0, "start"),
   LOG(1, "log"),
   FAIL(2, "fail"),
   ACCEPT(3, "accept");

   private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction((mode) -> {
      return mode.index;
   }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final Codec CODEC = StringIdentifiable.createCodec(TestBlockMode::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, (mode) -> {
      return mode.index;
   });
   private final int index;
   private final String id;
   private final Text name;
   private final Text info;

   private TestBlockMode(final int index, final String id) {
      this.index = index;
      this.id = id;
      this.name = Text.translatable("test_block.mode." + id);
      this.info = Text.translatable("test_block.mode_info." + id);
   }

   public String asString() {
      return this.id;
   }

   public Text getName() {
      return this.name;
   }

   public Text getInfo() {
      return this.info;
   }

   // $FF: synthetic method
   private static TestBlockMode[] method_66785() {
      return new TestBlockMode[]{START, LOG, FAIL, ACCEPT};
   }
}
