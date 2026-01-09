package net.minecraft.nbt;

import java.util.Optional;

public sealed interface AbstractNbtNumber extends NbtPrimitive permits NbtByte, NbtShort, NbtInt, NbtLong, NbtFloat, NbtDouble {
   byte byteValue();

   short shortValue();

   int intValue();

   long longValue();

   float floatValue();

   double doubleValue();

   Number numberValue();

   default Optional asNumber() {
      return Optional.of(this.numberValue());
   }

   default Optional asByte() {
      return Optional.of(this.byteValue());
   }

   default Optional asShort() {
      return Optional.of(this.shortValue());
   }

   default Optional asInt() {
      return Optional.of(this.intValue());
   }

   default Optional asLong() {
      return Optional.of(this.longValue());
   }

   default Optional asFloat() {
      return Optional.of(this.floatValue());
   }

   default Optional asDouble() {
      return Optional.of(this.doubleValue());
   }

   default Optional asBoolean() {
      return Optional.of(this.byteValue() != 0);
   }
}
