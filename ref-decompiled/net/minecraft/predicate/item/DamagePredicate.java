package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentPredicate;

public record DamagePredicate(NumberRange.IntRange durability, NumberRange.IntRange damage) implements ComponentPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("durability", NumberRange.IntRange.ANY).forGetter(DamagePredicate::durability), NumberRange.IntRange.CODEC.optionalFieldOf("damage", NumberRange.IntRange.ANY).forGetter(DamagePredicate::damage)).apply(instance, DamagePredicate::new);
   });

   public DamagePredicate(NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
      this.durability = intRange;
      this.damage = intRange2;
   }

   public boolean test(ComponentsAccess components) {
      Integer integer = (Integer)components.get(DataComponentTypes.DAMAGE);
      if (integer == null) {
         return false;
      } else {
         int i = (Integer)components.getOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
         if (!this.durability.test(i - integer)) {
            return false;
         } else {
            return this.damage.test(integer);
         }
      }
   }

   public static DamagePredicate durability(NumberRange.IntRange durability) {
      return new DamagePredicate(durability, NumberRange.IntRange.ANY);
   }

   public NumberRange.IntRange durability() {
      return this.durability;
   }

   public NumberRange.IntRange damage() {
      return this.damage;
   }
}
