package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

public interface SimpleNbtScanner extends NbtScanner {
   SimpleNbtScanner NOOP = new SimpleNbtScanner() {
   };

   default NbtScanner.Result visitEnd() {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitString(String value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitByte(byte value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitShort(short value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitInt(int value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitLong(long value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitFloat(float value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitDouble(double value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitByteArray(byte[] value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitIntArray(int[] value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitLongArray(long[] value) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result visitListMeta(NbtType entryType, int length) {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.NestedResult startListItem(NbtType type, int index) {
      return NbtScanner.NestedResult.SKIP;
   }

   default NbtScanner.NestedResult visitSubNbtType(NbtType type) {
      return NbtScanner.NestedResult.SKIP;
   }

   default NbtScanner.NestedResult startSubNbt(NbtType type, String key) {
      return NbtScanner.NestedResult.SKIP;
   }

   default NbtScanner.Result endNested() {
      return NbtScanner.Result.CONTINUE;
   }

   default NbtScanner.Result start(NbtType rootType) {
      return NbtScanner.Result.CONTINUE;
   }
}
