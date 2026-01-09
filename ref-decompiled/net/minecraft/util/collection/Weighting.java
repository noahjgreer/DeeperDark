package net.minecraft.util.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class Weighting {
   private Weighting() {
   }

   public static int getWeightSum(List pool, ToIntFunction weightGetter) {
      long l = 0L;

      Object object;
      for(Iterator var4 = pool.iterator(); var4.hasNext(); l += (long)weightGetter.applyAsInt(object)) {
         object = var4.next();
      }

      if (l > 2147483647L) {
         throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
      } else {
         return (int)l;
      }
   }

   public static Optional getRandom(Random random, List pool, int totalWeight, ToIntFunction weightGetter) {
      if (totalWeight < 0) {
         throw (IllegalArgumentException)Util.getFatalOrPause(new IllegalArgumentException("Negative total weight in getRandomItem"));
      } else if (totalWeight == 0) {
         return Optional.empty();
      } else {
         int i = random.nextInt(totalWeight);
         return getAt(pool, i, weightGetter);
      }
   }

   public static Optional getAt(List pool, int totalWeight, ToIntFunction weightGetter) {
      Iterator var3 = pool.iterator();

      Object object;
      do {
         if (!var3.hasNext()) {
            return Optional.empty();
         }

         object = var3.next();
         totalWeight -= weightGetter.applyAsInt(object);
      } while(totalWeight >= 0);

      return Optional.of(object);
   }

   public static Optional getRandom(Random random, List pool, ToIntFunction weightGetter) {
      return getRandom(random, pool, getWeightSum(pool, weightGetter), weightGetter);
   }
}
