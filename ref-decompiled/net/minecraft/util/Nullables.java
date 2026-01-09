package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class Nullables {
   /** @deprecated */
   @Deprecated
   public static Object requireNonNullElse(@Nullable Object first, Object second) {
      return Objects.requireNonNullElse(first, second);
   }

   @Nullable
   public static Object map(@Nullable Object value, Function mapper) {
      return value == null ? null : mapper.apply(value);
   }

   public static Object mapOrElse(@Nullable Object value, Function mapper, Object other) {
      return value == null ? other : mapper.apply(value);
   }

   public static Object mapOrElseGet(@Nullable Object value, Function mapper, Supplier getter) {
      return value == null ? getter.get() : mapper.apply(value);
   }

   @Nullable
   public static Object getFirst(Collection collection) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : null;
   }

   public static Object getFirstOrElse(Collection collection, Object defaultValue) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : defaultValue;
   }

   public static Object getFirstOrElseGet(Collection collection, Supplier getter) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : getter.get();
   }

   public static boolean isEmpty(@Nullable Object[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable boolean[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable byte[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable char[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable short[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable int[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable long[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable float[] array) {
      return array == null || array.length == 0;
   }

   public static boolean isEmpty(@Nullable double[] array) {
      return array == null || array.length == 0;
   }
}
