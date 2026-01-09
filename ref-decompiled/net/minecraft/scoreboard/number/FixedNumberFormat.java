package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class FixedNumberFormat implements NumberFormat {
   public static final NumberFormatType TYPE = new NumberFormatType() {
      private static final MapCodec CODEC;
      private static final PacketCodec PACKET_CODEC;

      public MapCodec getCodec() {
         return CODEC;
      }

      public PacketCodec getPacketCodec() {
         return PACKET_CODEC;
      }

      static {
         CODEC = TextCodecs.CODEC.fieldOf("value").xmap(FixedNumberFormat::new, (format) -> {
            return format.text;
         });
         PACKET_CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, (format) -> {
            return format.text;
         }, FixedNumberFormat::new);
      }
   };
   final Text text;

   public FixedNumberFormat(Text text) {
      this.text = text;
   }

   public MutableText format(int number) {
      return this.text.copy();
   }

   public NumberFormatType getType() {
      return TYPE;
   }
}
