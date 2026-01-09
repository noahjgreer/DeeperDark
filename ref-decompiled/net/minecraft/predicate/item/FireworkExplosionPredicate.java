package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.predicate.component.ComponentSubPredicate;

public record FireworkExplosionPredicate(Predicate predicate) implements ComponentSubPredicate {
   public static final Codec CODEC;

   public FireworkExplosionPredicate(Predicate predicate) {
      this.predicate = predicate;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.FIREWORK_EXPLOSION;
   }

   public boolean test(FireworkExplosionComponent fireworkExplosionComponent) {
      return this.predicate.test(fireworkExplosionComponent);
   }

   public Predicate predicate() {
      return this.predicate;
   }

   static {
      CODEC = FireworkExplosionPredicate.Predicate.CODEC.xmap(FireworkExplosionPredicate::new, FireworkExplosionPredicate::predicate);
   }

   public static record Predicate(Optional shape, Optional twinkle, Optional trail) implements java.util.function.Predicate {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(FireworkExplosionComponent.Type.CODEC.optionalFieldOf("shape").forGetter(Predicate::shape), Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(Predicate::twinkle), Codec.BOOL.optionalFieldOf("has_trail").forGetter(Predicate::trail)).apply(instance, Predicate::new);
      });

      public Predicate(Optional optional, Optional optional2, Optional optional3) {
         this.shape = optional;
         this.twinkle = optional2;
         this.trail = optional3;
      }

      public boolean test(FireworkExplosionComponent fireworkExplosionComponent) {
         if (this.shape.isPresent() && this.shape.get() != fireworkExplosionComponent.shape()) {
            return false;
         } else if (this.twinkle.isPresent() && (Boolean)this.twinkle.get() != fireworkExplosionComponent.hasTwinkle()) {
            return false;
         } else {
            return !this.trail.isPresent() || (Boolean)this.trail.get() == fireworkExplosionComponent.hasTrail();
         }
      }

      public Optional shape() {
         return this.shape;
      }

      public Optional twinkle() {
         return this.twinkle;
      }

      public Optional trail() {
         return this.trail;
      }

      // $FF: synthetic method
      public boolean test(final Object fireworkExplosionComponent) {
         return this.test((FireworkExplosionComponent)fireworkExplosionComponent);
      }
   }
}
