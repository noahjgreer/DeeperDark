package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public sealed interface NbtElement permits NbtCompound, AbstractNbtList, NbtPrimitive, NbtEnd {
   int field_33246 = 8;
   int field_33247 = 12;
   int field_33248 = 4;
   int field_33249 = 28;
   byte END_TYPE = 0;
   byte BYTE_TYPE = 1;
   byte SHORT_TYPE = 2;
   byte INT_TYPE = 3;
   byte LONG_TYPE = 4;
   byte FLOAT_TYPE = 5;
   byte DOUBLE_TYPE = 6;
   byte BYTE_ARRAY_TYPE = 7;
   byte STRING_TYPE = 8;
   byte LIST_TYPE = 9;
   byte COMPOUND_TYPE = 10;
   byte INT_ARRAY_TYPE = 11;
   byte LONG_ARRAY_TYPE = 12;
   int MAX_DEPTH = 512;

   void write(DataOutput output) throws IOException;

   String toString();

   byte getType();

   NbtType getNbtType();

   NbtElement copy();

   int getSizeInBytes();

   void accept(NbtElementVisitor visitor);

   NbtScanner.Result doAccept(NbtScanner visitor);

   default void accept(NbtScanner visitor) {
      NbtScanner.Result result = visitor.start(this.getNbtType());
      if (result == NbtScanner.Result.CONTINUE) {
         this.doAccept(visitor);
      }

   }

   default Optional asString() {
      return Optional.empty();
   }

   default Optional asNumber() {
      return Optional.empty();
   }

   default Optional asByte() {
      return this.asNumber().map(Number::byteValue);
   }

   default Optional asShort() {
      return this.asNumber().map(Number::shortValue);
   }

   default Optional asInt() {
      return this.asNumber().map(Number::intValue);
   }

   default Optional asLong() {
      return this.asNumber().map(Number::longValue);
   }

   default Optional asFloat() {
      return this.asNumber().map(Number::floatValue);
   }

   default Optional asDouble() {
      return this.asNumber().map(Number::doubleValue);
   }

   default Optional asBoolean() {
      return this.asByte().map((b) -> {
         return b != 0;
      });
   }

   default Optional asByteArray() {
      return Optional.empty();
   }

   default Optional asIntArray() {
      return Optional.empty();
   }

   default Optional asLongArray() {
      return Optional.empty();
   }

   default Optional asCompound() {
      return Optional.empty();
   }

   default Optional asNbtList() {
      return Optional.empty();
   }
}
