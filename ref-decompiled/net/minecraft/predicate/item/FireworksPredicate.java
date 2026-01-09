package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;

public record FireworksPredicate(Optional explosions, NumberRange.IntRange flightDuration) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(FireworkExplosionPredicate.Predicate.CODEC).optionalFieldOf("explosions").forGetter(FireworksPredicate::explosions), NumberRange.IntRange.CODEC.optionalFieldOf("flight_duration", NumberRange.IntRange.ANY).forGetter(FireworksPredicate::flightDuration)).apply(instance, FireworksPredicate::new);
   });

   public FireworksPredicate(Optional optional, NumberRange.IntRange intRange) {
      this.explosions = optional;
      this.flightDuration = intRange;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.FIREWORKS;
   }

   public boolean test(FireworksComponent fireworksComponent) {
      if (this.explosions.isPresent() && !((CollectionPredicate)this.explosions.get()).test((Iterable)fireworksComponent.explosions())) {
         return false;
      } else {
         return this.flightDuration.test(fireworksComponent.flightDuration());
      }
   }

   public Optional explosions() {
      return this.explosions;
   }

   public NumberRange.IntRange flightDuration() {
      return this.flightDuration;
   }
}
