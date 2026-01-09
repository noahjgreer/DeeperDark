package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class BufferHelper {
   public static final int field_29792 = 1460;
   public static final char[] HEX_CHARS_LOOKUP = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String getString(byte[] buf, int i, int j) {
      int k = j - 1;

      int l;
      for(l = i > k ? k : i; 0 != buf[l] && l < k; ++l) {
      }

      return new String(buf, i, l - i, StandardCharsets.UTF_8);
   }

   public static int getIntLE(byte[] buf, int start) {
      return getIntLE(buf, start, buf.length);
   }

   public static int getIntLE(byte[] buf, int start, int limit) {
      return 0 > limit - start - 4 ? 0 : buf[start + 3] << 24 | (buf[start + 2] & 255) << 16 | (buf[start + 1] & 255) << 8 | buf[start] & 255;
   }

   public static int getIntBE(byte[] buf, int start, int limit) {
      return 0 > limit - start - 4 ? 0 : buf[start] << 24 | (buf[start + 1] & 255) << 16 | (buf[start + 2] & 255) << 8 | buf[start + 3] & 255;
   }

   public static String toHex(byte b) {
      char var10000 = HEX_CHARS_LOOKUP[(b & 240) >>> 4];
      return "" + var10000 + HEX_CHARS_LOOKUP[b & 15];
   }
}
