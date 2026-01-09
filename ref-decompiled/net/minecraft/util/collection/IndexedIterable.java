package net.minecraft.util.collection;

import org.jetbrains.annotations.Nullable;

public interface IndexedIterable extends Iterable {
   int ABSENT_RAW_ID = -1;

   int getRawId(Object value);

   @Nullable
   Object get(int index);

   default Object getOrThrow(int index) {
      Object object = this.get(index);
      if (object == null) {
         throw new IllegalArgumentException("No value with id " + index);
      } else {
         return object;
      }
   }

   default int getRawIdOrThrow(Object value) {
      int i = this.getRawId(value);
      if (i == -1) {
         String var10002 = String.valueOf(value);
         throw new IllegalArgumentException("Can't find id for '" + var10002 + "' in map " + String.valueOf(this));
      } else {
         return i;
      }
   }

   int size();
}
