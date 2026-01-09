package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.random.Random;

public record BinomialLootNumberProvider(LootNumberProvider n, LootNumberProvider p) implements LootNumberProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootNumberProviderTypes.CODEC.fieldOf("n").forGetter(BinomialLootNumberProvider::n), LootNumberProviderTypes.CODEC.fieldOf("p").forGetter(BinomialLootNumberProvider::p)).apply(instance, BinomialLootNumberProvider::new);
   });

   public BinomialLootNumberProvider(LootNumberProvider n, LootNumberProvider p) {
      this.n = n;
      this.p = p;
   }

   public LootNumberProviderType getType() {
      return LootNumberProviderTypes.BINOMIAL;
   }

   public int nextInt(LootContext context) {
      int i = this.n.nextInt(context);
      float f = this.p.nextFloat(context);
      Random random = context.getRandom();
      int j = 0;

      for(int k = 0; k < i; ++k) {
         if (random.nextFloat() < f) {
            ++j;
         }
      }

      return j;
   }

   public float nextFloat(LootContext context) {
      return (float)this.nextInt(context);
   }

   public static BinomialLootNumberProvider create(int n, float p) {
      return new BinomialLootNumberProvider(ConstantLootNumberProvider.create((float)n), ConstantLootNumberProvider.create(p));
   }

   public Set getAllowedParameters() {
      return Sets.union(this.n.getAllowedParameters(), this.p.getAllowedParameters());
   }

   public LootNumberProvider n() {
      return this.n;
   }

   public LootNumberProvider p() {
      return this.p;
   }
}
