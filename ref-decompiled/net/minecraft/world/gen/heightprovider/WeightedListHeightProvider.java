package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;

public class WeightedListHeightProvider extends HeightProvider {
   public static final MapCodec WEIGHTED_LIST_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Pool.createNonEmptyCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((weightedListHeightProvider) -> {
         return weightedListHeightProvider.weightedList;
      })).apply(instance, WeightedListHeightProvider::new);
   });
   private final Pool weightedList;

   public WeightedListHeightProvider(Pool weightedList) {
      this.weightedList = weightedList;
   }

   public int get(Random random, HeightContext context) {
      return ((HeightProvider)this.weightedList.get(random)).get(random, context);
   }

   public HeightProviderType getType() {
      return HeightProviderType.WEIGHTED_LIST;
   }
}
