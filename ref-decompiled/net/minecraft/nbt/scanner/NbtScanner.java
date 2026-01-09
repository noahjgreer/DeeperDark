package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

public interface NbtScanner {
   Result visitEnd();

   Result visitString(String value);

   Result visitByte(byte value);

   Result visitShort(short value);

   Result visitInt(int value);

   Result visitLong(long value);

   Result visitFloat(float value);

   Result visitDouble(double value);

   Result visitByteArray(byte[] value);

   Result visitIntArray(int[] value);

   Result visitLongArray(long[] value);

   Result visitListMeta(NbtType entryType, int length);

   NestedResult visitSubNbtType(NbtType type);

   NestedResult startSubNbt(NbtType type, String key);

   NestedResult startListItem(NbtType type, int index);

   Result endNested();

   Result start(NbtType rootType);

   public static enum NestedResult {
      ENTER,
      SKIP,
      BREAK,
      HALT;

      // $FF: synthetic method
      private static NestedResult[] method_39873() {
         return new NestedResult[]{ENTER, SKIP, BREAK, HALT};
      }
   }

   public static enum Result {
      CONTINUE,
      BREAK,
      HALT;

      // $FF: synthetic method
      private static Result[] method_39874() {
         return new Result[]{CONTINUE, BREAK, HALT};
      }
   }
}
