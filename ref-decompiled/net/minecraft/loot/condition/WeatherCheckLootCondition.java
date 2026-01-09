package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;

public record WeatherCheckLootCondition(Optional raining, Optional thundering) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherCheckLootCondition::raining), Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherCheckLootCondition::thundering)).apply(instance, WeatherCheckLootCondition::new);
   });

   public WeatherCheckLootCondition(Optional optional, Optional optional2) {
      this.raining = optional;
      this.thundering = optional2;
   }

   public LootConditionType getType() {
      return LootConditionTypes.WEATHER_CHECK;
   }

   public boolean test(LootContext lootContext) {
      ServerWorld serverWorld = lootContext.getWorld();
      if (this.raining.isPresent() && (Boolean)this.raining.get() != serverWorld.isRaining()) {
         return false;
      } else {
         return !this.thundering.isPresent() || (Boolean)this.thundering.get() == serverWorld.isThundering();
      }
   }

   public static Builder create() {
      return new Builder();
   }

   public Optional raining() {
      return this.raining;
   }

   public Optional thundering() {
      return this.thundering;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }

   public static class Builder implements LootCondition.Builder {
      private Optional raining = Optional.empty();
      private Optional thundering = Optional.empty();

      public Builder raining(boolean raining) {
         this.raining = Optional.of(raining);
         return this;
      }

      public Builder thundering(boolean thundering) {
         this.thundering = Optional.of(thundering);
         return this;
      }

      public WeatherCheckLootCondition build() {
         return new WeatherCheckLootCondition(this.raining, this.thundering);
      }

      // $FF: synthetic method
      public LootCondition build() {
         return this.build();
      }
   }
}
