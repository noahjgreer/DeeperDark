package net.minecraft.util.math;

import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.util.Util;

public class Quantiles {
   public static final com.google.common.math.Quantiles.ScaleAndIndexes QUANTILE_POINTS = com.google.common.math.Quantiles.scale(100).indexes(new int[]{50, 75, 90, 99});

   private Quantiles() {
   }

   public static Map create(long[] values) {
      return values.length == 0 ? Map.of() : reverseMap(QUANTILE_POINTS.compute(values));
   }

   public static Map create(double[] values) {
      return values.length == 0 ? Map.of() : reverseMap(QUANTILE_POINTS.compute(values));
   }

   private static Map reverseMap(Map map) {
      Int2DoubleSortedMap int2DoubleSortedMap = (Int2DoubleSortedMap)Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), (reversedMap) -> {
         reversedMap.putAll(map);
      });
      return Int2DoubleSortedMaps.unmodifiable(int2DoubleSortedMap);
   }
}
