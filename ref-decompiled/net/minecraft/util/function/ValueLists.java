package net.minecraft.util.function;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.math.MathHelper;

public class ValueLists {
   private static IntFunction createIndexToValueFunction(ToIntFunction valueToIndexFunction, Object[] values) {
      if (values.length == 0) {
         throw new IllegalArgumentException("Empty value list");
      } else {
         Int2ObjectMap int2ObjectMap = new Int2ObjectOpenHashMap();
         Object[] var3 = values;
         int var4 = values.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object object = var3[var5];
            int i = valueToIndexFunction.applyAsInt(object);
            Object object2 = int2ObjectMap.put(i, object);
            if (object2 != null) {
               throw new IllegalArgumentException("Duplicate entry on id " + i + ": current=" + String.valueOf(object) + ", previous=" + String.valueOf(object2));
            }
         }

         return int2ObjectMap;
      }
   }

   public static IntFunction createIndexToValueFunction(ToIntFunction valueToIndexFunction, Object[] values, Object fallback) {
      IntFunction intFunction = createIndexToValueFunction(valueToIndexFunction, values);
      return (index) -> {
         return Objects.requireNonNullElse(intFunction.apply(index), fallback);
      };
   }

   private static Object[] validate(ToIntFunction valueToIndexFunction, Object[] values) {
      int i = values.length;
      if (i == 0) {
         throw new IllegalArgumentException("Empty value list");
      } else {
         Object[] objects = (Object[])values.clone();
         Arrays.fill(objects, (Object)null);
         Object[] var4 = values;
         int var5 = values.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object object = var4[var6];
            int j = valueToIndexFunction.applyAsInt(object);
            if (j < 0 || j >= i) {
               throw new IllegalArgumentException("Values are not continous, found index " + j + " for value " + String.valueOf(object));
            }

            Object object2 = objects[j];
            if (object2 != null) {
               throw new IllegalArgumentException("Duplicate entry on id " + j + ": current=" + String.valueOf(object) + ", previous=" + String.valueOf(object2));
            }

            objects[j] = object;
         }

         for(int k = 0; k < i; ++k) {
            if (objects[k] == null) {
               throw new IllegalArgumentException("Missing value at index: " + k);
            }
         }

         return objects;
      }
   }

   public static IntFunction createIndexToValueFunction(ToIntFunction valueToIndexFunction, Object[] values, OutOfBoundsHandling outOfBoundsHandling) {
      Object[] objects = validate(valueToIndexFunction, values);
      int i = objects.length;
      IntFunction var10000;
      switch (outOfBoundsHandling.ordinal()) {
         case 0:
            Object object = objects[0];
            var10000 = (index) -> {
               return index >= 0 && index < i ? objects[index] : object;
            };
            break;
         case 1:
            var10000 = (index) -> {
               return objects[MathHelper.floorMod(index, i)];
            };
            break;
         case 2:
            var10000 = (index) -> {
               return objects[MathHelper.clamp(index, 0, i - 1)];
            };
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum OutOfBoundsHandling {
      ZERO,
      WRAP,
      CLAMP;

      // $FF: synthetic method
      private static OutOfBoundsHandling[] method_47919() {
         return new OutOfBoundsHandling[]{ZERO, WRAP, CLAMP};
      }
   }
}
