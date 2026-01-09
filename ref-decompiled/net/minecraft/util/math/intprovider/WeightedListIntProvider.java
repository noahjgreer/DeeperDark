package net.minecraft.util.math.intprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

public class WeightedListIntProvider extends IntProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Pool.createNonEmptyCodec(IntProvider.VALUE_CODEC).fieldOf("distribution").forGetter((provider) -> {
         return provider.weightedList;
      })).apply(instance, WeightedListIntProvider::new);
   });
   private final Pool weightedList;
   private final int min;
   private final int max;

   public WeightedListIntProvider(Pool weightedList) {
      this.weightedList = weightedList;
      int i = Integer.MAX_VALUE;
      int j = Integer.MIN_VALUE;

      int l;
      for(Iterator var4 = weightedList.getEntries().iterator(); var4.hasNext(); j = Math.max(j, l)) {
         Weighted weighted = (Weighted)var4.next();
         int k = ((IntProvider)weighted.value()).getMin();
         l = ((IntProvider)weighted.value()).getMax();
         i = Math.min(i, k);
      }

      this.min = i;
      this.max = j;
   }

   public int get(Random random) {
      return ((IntProvider)this.weightedList.get(random)).get(random);
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public IntProviderType getType() {
      return IntProviderType.WEIGHTED_LIST;
   }
}
