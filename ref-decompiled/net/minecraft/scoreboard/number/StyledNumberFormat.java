package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StyledNumberFormat implements NumberFormat {
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
         CODEC = Style.Codecs.MAP_CODEC.xmap(StyledNumberFormat::new, (format) -> {
            return format.style;
         });
         PACKET_CODEC = PacketCodec.tuple(Style.Codecs.PACKET_CODEC, (format) -> {
            return format.style;
         }, StyledNumberFormat::new);
      }
   };
   public static final StyledNumberFormat EMPTY;
   public static final StyledNumberFormat RED;
   public static final StyledNumberFormat YELLOW;
   final Style style;

   public StyledNumberFormat(Style style) {
      this.style = style;
   }

   public MutableText format(int number) {
      return Text.literal(Integer.toString(number)).fillStyle(this.style);
   }

   public NumberFormatType getType() {
      return TYPE;
   }

   static {
      EMPTY = new StyledNumberFormat(Style.EMPTY);
      RED = new StyledNumberFormat(Style.EMPTY.withColor(Formatting.RED));
      YELLOW = new StyledNumberFormat(Style.EMPTY.withColor(Formatting.YELLOW));
   }
}
