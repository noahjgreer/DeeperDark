package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public enum Rarity implements StringIdentifiable {
   COMMON(0, "common", Formatting.WHITE),
   UNCOMMON(1, "uncommon", Formatting.YELLOW),
   RARE(2, "rare", Formatting.AQUA),
   EPIC(3, "epic", Formatting.LIGHT_PURPLE);

   public static final Codec CODEC = StringIdentifiable.createBasicCodec(Rarity::values);
   public static final IntFunction ID_TO_VALUE = ValueLists.createIndexToValueFunction((value) -> {
      return value.index;
   }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(ID_TO_VALUE, (value) -> {
      return value.index;
   });
   private final int index;
   private final String name;
   private final Formatting formatting;

   private Rarity(final int index, final String name, final Formatting formatting) {
      this.index = index;
      this.name = name;
      this.formatting = formatting;
   }

   public Formatting getFormatting() {
      return this.formatting;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static Rarity[] method_36683() {
      return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
   }
}
