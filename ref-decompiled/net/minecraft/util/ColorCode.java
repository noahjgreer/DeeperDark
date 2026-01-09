package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public record ColorCode(int rgba) {
   private static final String HASH = "#";
   public static final Codec CODEC;

   public ColorCode(int i) {
      this.rgba = i;
   }

   private String asString() {
      return String.format(Locale.ROOT, "#%08X", this.rgba);
   }

   public String toString() {
      return this.asString();
   }

   public int rgba() {
      return this.rgba;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((code) -> {
         if (!code.startsWith("#")) {
            return DataResult.error(() -> {
               return "Not a color code: " + code;
            });
         } else {
            try {
               int i = (int)Long.parseLong(code.substring(1), 16);
               return DataResult.success(new ColorCode(i));
            } catch (NumberFormatException var2) {
               return DataResult.error(() -> {
                  return "Exception parsing color code: " + var2.getMessage();
               });
            }
         }
      }, ColorCode::asString);
   }
}
