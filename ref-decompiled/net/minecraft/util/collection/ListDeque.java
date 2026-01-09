package net.minecraft.util.collection;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import java.util.SequencedCollection;
import org.jetbrains.annotations.Nullable;

public interface ListDeque extends Serializable, Cloneable, Deque, List, RandomAccess {
   ListDeque reversed();

   Object getFirst();

   Object getLast();

   void addFirst(Object value);

   void addLast(Object value);

   Object removeFirst();

   Object removeLast();

   default boolean offer(Object object) {
      return this.offerLast(object);
   }

   default Object remove() {
      return this.removeFirst();
   }

   @Nullable
   default Object poll() {
      return this.pollFirst();
   }

   default Object element() {
      return this.getFirst();
   }

   @Nullable
   default Object peek() {
      return this.peekFirst();
   }

   default void push(Object object) {
      this.addFirst(object);
   }

   default Object pop() {
      return this.removeFirst();
   }

   // $FF: synthetic method
   default List reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   default SequencedCollection reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   default Deque reversed() {
      return this.reversed();
   }
}
