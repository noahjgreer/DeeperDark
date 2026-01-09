package net.minecraft.util.collection;

import com.google.common.annotations.VisibleForTesting;
import java.util.AbstractList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

public class ArrayListDeque extends AbstractList implements ListDeque {
   private static final int MISSING = 1;
   private Object[] array;
   private int startIndex;
   private int size;

   public ArrayListDeque() {
      this(1);
   }

   public ArrayListDeque(int size) {
      this.array = new Object[size];
      this.startIndex = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   @VisibleForTesting
   public int getArrayLength() {
      return this.array.length;
   }

   private int wrap(int index) {
      return (index + this.startIndex) % this.array.length;
   }

   public Object get(int index) {
      this.checkBounds(index);
      return this.getRaw(this.wrap(index));
   }

   private static void checkBounds(int start, int end) {
      if (start < 0 || start >= end) {
         throw new IndexOutOfBoundsException(start);
      }
   }

   private void checkBounds(int index) {
      checkBounds(index, this.size);
   }

   private Object getRaw(int index) {
      return this.array[index];
   }

   public Object set(int index, Object value) {
      this.checkBounds(index);
      Objects.requireNonNull(value);
      int i = this.wrap(index);
      Object object = this.getRaw(i);
      this.array[i] = value;
      return object;
   }

   public void add(int index, Object value) {
      checkBounds(index, this.size + 1);
      Objects.requireNonNull(value);
      if (this.size == this.array.length) {
         this.enlarge();
      }

      int i = this.wrap(index);
      if (index == this.size) {
         this.array[i] = value;
      } else if (index == 0) {
         --this.startIndex;
         if (this.startIndex < 0) {
            this.startIndex += this.array.length;
         }

         this.array[this.wrap(0)] = value;
      } else {
         for(int j = this.size - 1; j >= index; --j) {
            this.array[this.wrap(j + 1)] = this.array[this.wrap(j)];
         }

         this.array[i] = value;
      }

      ++this.modCount;
      ++this.size;
   }

   private void enlarge() {
      int i = this.array.length + Math.max(this.array.length >> 1, 1);
      Object[] objects = new Object[i];
      this.copyTo(objects, this.size);
      this.startIndex = 0;
      this.array = objects;
   }

   public Object remove(int index) {
      this.checkBounds(index);
      int i = this.wrap(index);
      Object object = this.getRaw(i);
      if (index == 0) {
         this.array[i] = null;
         ++this.startIndex;
      } else if (index == this.size - 1) {
         this.array[i] = null;
      } else {
         for(int j = index + 1; j < this.size; ++j) {
            this.array[this.wrap(j - 1)] = this.get(j);
         }

         this.array[this.wrap(this.size - 1)] = null;
      }

      ++this.modCount;
      --this.size;
      return object;
   }

   public boolean removeIf(Predicate predicate) {
      int i = 0;

      for(int j = 0; j < this.size; ++j) {
         Object object = this.get(j);
         if (predicate.test(object)) {
            ++i;
         } else if (i != 0) {
            this.array[this.wrap(j - i)] = object;
            this.array[this.wrap(j)] = null;
         }
      }

      this.modCount += i;
      this.size -= i;
      return i != 0;
   }

   private void copyTo(Object[] array, int size) {
      for(int i = 0; i < size; ++i) {
         array[i] = this.get(i);
      }

   }

   public void replaceAll(UnaryOperator mapper) {
      for(int i = 0; i < this.size; ++i) {
         int j = this.wrap(i);
         this.array[j] = Objects.requireNonNull(mapper.apply(this.getRaw(i)));
      }

   }

   public void forEach(Consumer consumer) {
      for(int i = 0; i < this.size; ++i) {
         consumer.accept(this.get(i));
      }

   }

   public void addFirst(Object value) {
      this.add(0, value);
   }

   public void addLast(Object value) {
      this.add(this.size, value);
   }

   public boolean offerFirst(Object value) {
      this.addFirst(value);
      return true;
   }

   public boolean offerLast(Object value) {
      this.addLast(value);
      return true;
   }

   public Object removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(0);
      }
   }

   public Object removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size - 1);
      }
   }

   public ListDeque reversed() {
      return new ReversedWrapper(this);
   }

   @Nullable
   public Object pollFirst() {
      return this.size == 0 ? null : this.removeFirst();
   }

   @Nullable
   public Object pollLast() {
      return this.size == 0 ? null : this.removeLast();
   }

   public Object getFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(0);
      }
   }

   public Object getLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size - 1);
      }
   }

   @Nullable
   public Object peekFirst() {
      return this.size == 0 ? null : this.getFirst();
   }

   @Nullable
   public Object peekLast() {
      return this.size == 0 ? null : this.getLast();
   }

   public boolean removeFirstOccurrence(Object value) {
      for(int i = 0; i < this.size; ++i) {
         Object object = this.get(i);
         if (Objects.equals(value, object)) {
            this.remove(i);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(Object value) {
      for(int i = this.size - 1; i >= 0; --i) {
         Object object = this.get(i);
         if (Objects.equals(value, object)) {
            this.remove(i);
            return true;
         }
      }

      return false;
   }

   public Iterator descendingIterator() {
      return new IteratorImpl();
   }

   // $FF: synthetic method
   public List reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   public SequencedCollection reversed() {
      return this.reversed();
   }

   // $FF: synthetic method
   public Deque reversed() {
      return this.reversed();
   }

   class ReversedWrapper extends AbstractList implements ListDeque {
      private final ArrayListDeque original;

      public ReversedWrapper(final ArrayListDeque original) {
         this.original = original;
      }

      public ListDeque reversed() {
         return this.original;
      }

      public Object getFirst() {
         return this.original.getLast();
      }

      public Object getLast() {
         return this.original.getFirst();
      }

      public void addFirst(Object object) {
         this.original.addLast(object);
      }

      public void addLast(Object object) {
         this.original.addFirst(object);
      }

      public boolean offerFirst(Object value) {
         return this.original.offerLast(value);
      }

      public boolean offerLast(Object value) {
         return this.original.offerFirst(value);
      }

      public Object pollFirst() {
         return this.original.pollLast();
      }

      public Object pollLast() {
         return this.original.pollFirst();
      }

      public Object peekFirst() {
         return this.original.peekLast();
      }

      public Object peekLast() {
         return this.original.peekFirst();
      }

      public Object removeFirst() {
         return this.original.removeLast();
      }

      public Object removeLast() {
         return this.original.removeFirst();
      }

      public boolean removeFirstOccurrence(Object value) {
         return this.original.removeLastOccurrence(value);
      }

      public boolean removeLastOccurrence(Object value) {
         return this.original.removeFirstOccurrence(value);
      }

      public Iterator descendingIterator() {
         return this.original.iterator();
      }

      public int size() {
         return this.original.size();
      }

      public boolean isEmpty() {
         return this.original.isEmpty();
      }

      public boolean contains(Object value) {
         return this.original.contains(value);
      }

      public Object get(int index) {
         return this.original.get(this.getReversedIndex(index));
      }

      public Object set(int index, Object value) {
         return this.original.set(this.getReversedIndex(index), value);
      }

      public void add(int index, Object value) {
         this.original.add(this.getReversedIndex(index) + 1, value);
      }

      public Object remove(int index) {
         return this.original.remove(this.getReversedIndex(index));
      }

      public int indexOf(Object value) {
         return this.getReversedIndex(this.original.lastIndexOf(value));
      }

      public int lastIndexOf(Object value) {
         return this.getReversedIndex(this.original.indexOf(value));
      }

      public List subList(int start, int end) {
         return this.original.subList(this.getReversedIndex(end) + 1, this.getReversedIndex(start) + 1).reversed();
      }

      public Iterator iterator() {
         return this.original.descendingIterator();
      }

      public void clear() {
         this.original.clear();
      }

      private int getReversedIndex(int index) {
         return index == -1 ? -1 : this.original.size() - 1 - index;
      }

      // $FF: synthetic method
      public List reversed() {
         return this.reversed();
      }

      // $FF: synthetic method
      public SequencedCollection reversed() {
         return this.reversed();
      }

      // $FF: synthetic method
      public Deque reversed() {
         return this.reversed();
      }
   }

   class IteratorImpl implements Iterator {
      private int currentIndex = ArrayListDeque.this.size() - 1;

      public IteratorImpl() {
      }

      public boolean hasNext() {
         return this.currentIndex >= 0;
      }

      public Object next() {
         return ArrayListDeque.this.get(this.currentIndex--);
      }

      public void remove() {
         ArrayListDeque.this.remove(this.currentIndex + 1);
      }
   }
}
