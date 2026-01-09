package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultedList extends AbstractList {
   private final List delegate;
   @Nullable
   private final Object initialElement;

   public static DefaultedList of() {
      return new DefaultedList(Lists.newArrayList(), (Object)null);
   }

   public static DefaultedList ofSize(int size) {
      return new DefaultedList(Lists.newArrayListWithCapacity(size), (Object)null);
   }

   public static DefaultedList ofSize(int size, Object defaultValue) {
      Validate.notNull(defaultValue);
      Object[] objects = new Object[size];
      Arrays.fill(objects, defaultValue);
      return new DefaultedList(Arrays.asList(objects), defaultValue);
   }

   @SafeVarargs
   public static DefaultedList copyOf(Object defaultValue, Object... values) {
      return new DefaultedList(Arrays.asList(values), defaultValue);
   }

   protected DefaultedList(List delegate, @Nullable Object initialElement) {
      this.delegate = delegate;
      this.initialElement = initialElement;
   }

   @NotNull
   public Object get(int index) {
      return this.delegate.get(index);
   }

   public Object set(int index, Object element) {
      Validate.notNull(element);
      return this.delegate.set(index, element);
   }

   public void add(int index, Object element) {
      Validate.notNull(element);
      this.delegate.add(index, element);
   }

   public Object remove(int index) {
      return this.delegate.remove(index);
   }

   public int size() {
      return this.delegate.size();
   }

   public void clear() {
      if (this.initialElement == null) {
         super.clear();
      } else {
         for(int i = 0; i < this.size(); ++i) {
            this.set(i, this.initialElement);
         }
      }

   }
}
