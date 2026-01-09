package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.server.world.ServerWorld;

public record TimeCheckLootCondition(Optional period, BoundedIntUnaryOperator value) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.LONG.optionalFieldOf("period").forGetter(TimeCheckLootCondition::period), BoundedIntUnaryOperator.CODEC.fieldOf("value").forGetter(TimeCheckLootCondition::value)).apply(instance, TimeCheckLootCondition::new);
   });

   public TimeCheckLootCondition(Optional optional, BoundedIntUnaryOperator value) {
      this.period = optional;
      this.value = value;
   }

   public LootConditionType getType() {
      return LootConditionTypes.TIME_CHECK;
   }

   public Set getAllowedParameters() {
      return this.value.getRequiredParameters();
   }

   public boolean test(LootContext lootContext) {
      ServerWorld serverWorld = lootContext.getWorld();
      long l = serverWorld.getTimeOfDay();
      if (this.period.isPresent()) {
         l %= (Long)this.period.get();
      }

      return this.value.test(lootContext, (int)l);
   }

   public static Builder create(BoundedIntUnaryOperator value) {
      return new Builder(value);
   }

   public Optional period() {
      return this.period;
   }

   public BoundedIntUnaryOperator value() {
      return this.value;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }

   public static class Builder implements LootCondition.Builder {
      private Optional period = Optional.empty();
      private final BoundedIntUnaryOperator value;

      public Builder(BoundedIntUnaryOperator value) {
         this.value = value;
      }

      public Builder period(long period) {
         this.period = Optional.of(period);
         return this;
      }

      public TimeCheckLootCondition build() {
         return new TimeCheckLootCondition(this.period, this.value);
      }

      // $FF: synthetic method
      public LootCondition build() {
         return this.build();
      }
   }
}
