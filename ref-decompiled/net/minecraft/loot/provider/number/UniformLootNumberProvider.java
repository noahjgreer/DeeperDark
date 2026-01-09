package net.minecraft.loot.provider.number;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.math.MathHelper;

public record UniformLootNumberProvider(LootNumberProvider min, LootNumberProvider max) implements LootNumberProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootNumberProviderTypes.CODEC.fieldOf("min").forGetter(UniformLootNumberProvider::min), LootNumberProviderTypes.CODEC.fieldOf("max").forGetter(UniformLootNumberProvider::max)).apply(instance, UniformLootNumberProvider::new);
   });

   public UniformLootNumberProvider(LootNumberProvider min, LootNumberProvider max) {
      this.min = min;
      this.max = max;
   }

   public LootNumberProviderType getType() {
      return LootNumberProviderTypes.UNIFORM;
   }

   public static UniformLootNumberProvider create(float min, float max) {
      return new UniformLootNumberProvider(ConstantLootNumberProvider.create(min), ConstantLootNumberProvider.create(max));
   }

   public int nextInt(LootContext context) {
      return MathHelper.nextInt(context.getRandom(), this.min.nextInt(context), this.max.nextInt(context));
   }

   public float nextFloat(LootContext context) {
      return MathHelper.nextFloat(context.getRandom(), this.min.nextFloat(context), this.max.nextFloat(context));
   }

   public Set getAllowedParameters() {
      return Sets.union(this.min.getAllowedParameters(), this.max.getAllowedParameters());
   }

   public LootNumberProvider min() {
      return this.min;
   }

   public LootNumberProvider max() {
      return this.max;
   }
}
