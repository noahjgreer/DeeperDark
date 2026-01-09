package net.minecraft.util.collection;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class SortedArraySet extends AbstractSet {
   private static final int DEFAULT_CAPACITY = 10;
   private final Comparator comparator;
   Object[] elements;
   int size;

   private SortedArraySet(int initialCapacity, Comparator comparator) {
      this.comparator = comparator;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Initial capacity (" + initialCapacity + ") is negative");
      } else {
         this.elements = cast(new Object[initialCapacity]);
      }
   }

   public static SortedArraySet create() {
      return create(10);
   }

   public static SortedArraySet create(int initialCapacity) {
      return new SortedArraySet(initialCapacity, Comparator.naturalOrder());
   }

   public static SortedArraySet create(Comparator comparator) {
      return create(comparator, 10);
   }

   public static SortedArraySet create(Comparator comparator, int initialCapacity) {
      return new SortedArraySet(initialCapacity, comparator);
   }

   private static Object[] cast(Object[] array) {
      return array;
   }

   private int binarySearch(Object object) {
      return Arrays.binarySearch(this.elements, 0, this.size, object, this.comparator);
   }

   private static int insertionPoint(int binarySearchResult) {
      return -binarySearchResult - 1;
   }

   public boolean add(Object object) {
      int i = this.binarySearch(object);
      if (i >= 0) {
         return false;
      } else {
         int j = insertionPoint(i);
         this.add(object, j);
         return true;
      }
   }

   private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.elements.length) {
         if (this.elements != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            minCapacity = Util.nextCapacity(this.elements.length, minCapacity);
         } else if (minCapacity < 10) {
            minCapacity = 10;
         }

         Object[] objects = new Object[minCapacity];
         System.arraycopy(this.elements, 0, objects, 0, this.size);
         this.elements = cast(objects);
      }
   }

   private void add(Object object, int index) {
      this.ensureCapacity(this.size + 1);
      if (index != this.size) {
         System.arraycopy(this.elements, index, this.elements, index + 1, this.size - index);
      }

      this.elements[index] = object;
      ++this.size;
   }

   void remove(int index) {
      --this.size;
      if (index != this.size) {
         System.arraycopy(this.elements, index + 1, this.elements, index, this.size - index);
      }

      this.elements[this.size] = null;
   }

   private Object get(int index) {
      return this.elements[index];
   }

   public Object addAndGet(Object object) {
      int i = this.binarySearch(object);
      if (i >= 0) {
         return this.get(i);
      } else {
         this.add(object, insertionPoint(i));
         return object;
      }
   }

   public boolean remove(Object object) {
      int i = this.binarySearch(object);
      if (i >= 0) {
         this.remove(i);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public Object getIfContains(Object object) {
      int i = this.binarySearch(object);
      return i >= 0 ? this.get(i) : null;
   }

   public Object first() {
      return this.get(0);
   }

   public Object last() {
      return this.get(this.size - 1);
   }

   public boolean contains(Object object) {
      int i = this.binarySearch(object);
      return i >= 0;
   }

   public Iterator iterator() {
      return new SetIterator();
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      return Arrays.copyOf(this.elements, this.size, Object[].class);
   }

   public Object[] toArray(Object[] array) {
      if (array.length < this.size) {
         return Arrays.copyOf(this.elements, this.size, array.getClass());
      } else {
         System.arraycopy(this.elements, 0, array, 0, this.size);
         if (array.length > this.size) {
            array[this.size] = null;
         }

         return array;
      }
   }

   public void clear() {
      Arrays.fill(this.elements, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof SortedArraySet) {
            SortedArraySet sortedArraySet = (SortedArraySet)o;
            if (this.comparator.equals(sortedArraySet.comparator)) {
               return this.size == sortedArraySet.size && Arrays.equals(this.elements, sortedArraySet.elements);
            }
         }

         return super.equals(o);
      }
   }

   private class SetIterator implements Iterator {
      private int nextIndex;
      private int lastIndex = -1;

      SetIterator() {
      }

      public boolean hasNext() {
         return this.nextIndex < SortedArraySet.this.size;
      }

      public Object next() {
         if (this.nextIndex >= SortedArraySet.this.size) {
            throw new NoSuchElementException();
         } else {
            this.lastIndex = this.nextIndex++;
            return SortedArraySet.this.elements[this.lastIndex];
         }
      }

      public void remove() {
         if (this.lastIndex == -1) {
            throw new IllegalStateException();
         } else {
            SortedArraySet.this.remove(this.lastIndex);
            --this.nextIndex;
            this.lastIndex = -1;
         }
      }
   }
}
