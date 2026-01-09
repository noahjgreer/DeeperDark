package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class StartedRidingCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return StartedRidingCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player) {
      this.trigger(player, (conditions) -> {
         return true;
      });
   }

   public static record Conditions(Optional player) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player)).apply(instance, Conditions::new);
      });

      public Conditions(Optional optional) {
         this.player = optional;
      }

      public static AdvancementCriterion create(EntityPredicate.Builder player) {
         return Criteria.STARTED_RIDING.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player))));
      }

      public Optional player() {
         return this.player;
      }
   }
}
