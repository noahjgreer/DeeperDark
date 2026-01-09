package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class BlankNumberFormat implements NumberFormat {
   public static final BlankNumberFormat INSTANCE = new BlankNumberFormat();
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
         CODEC = MapCodec.unit(BlankNumberFormat.INSTANCE);
         PACKET_CODEC = PacketCodec.unit(BlankNumberFormat.INSTANCE);
      }
   };

   public MutableText format(int number) {
      return Text.empty();
   }

   public NumberFormatType getType() {
      return TYPE;
   }
}
