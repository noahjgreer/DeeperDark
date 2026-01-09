package net.minecraft.nbt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public sealed interface AbstractNbtList extends Iterable, NbtElement permits NbtList, NbtByteArray, NbtIntArray, NbtLongArray {
   void clear();

   boolean setElement(int index, NbtElement element);

   boolean addElement(int index, NbtElement element);

   NbtElement method_10536(int i);

   NbtElement method_10534(int i);

   int size();

   default boolean isEmpty() {
      return this.size() == 0;
   }

   default Iterator iterator() {
      return new Iterator() {
         private int current;

         public boolean hasNext() {
            return this.current < AbstractNbtList.this.size();
         }

         public NbtElement next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return AbstractNbtList.this.method_10534(this.current++);
            }
         }

         // $FF: synthetic method
         public Object next() {
            return this.next();
         }
      };
   }

   default Stream stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }
}
