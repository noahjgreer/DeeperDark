package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return LevitationCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(player, startPos, duration);
      });
   }

   public static record Conditions(Optional player, Optional distance, NumberRange.IntRange duration) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(Conditions::distance), NumberRange.IntRange.CODEC.optionalFieldOf("duration", NumberRange.IntRange.ANY).forGetter(Conditions::duration)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional distance, NumberRange.IntRange duration) {
         this.player = playerPredicate;
         this.distance = distance;
         this.duration = duration;
      }

      public static AdvancementCriterion create(DistancePredicate distance) {
         return Criteria.LEVITATION.create(new Conditions(Optional.empty(), Optional.of(distance), NumberRange.IntRange.ANY));
      }

      public boolean matches(ServerPlayerEntity player, Vec3d distance, int duration) {
         if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).test(distance.x, distance.y, distance.z, player.getX(), player.getY(), player.getZ())) {
            return false;
         } else {
            return this.duration.test(duration);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional distance() {
         return this.distance;
      }

      public NumberRange.IntRange duration() {
         return this.duration;
      }
   }
}
