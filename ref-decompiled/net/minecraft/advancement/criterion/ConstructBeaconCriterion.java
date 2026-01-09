package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConstructBeaconCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ConstructBeaconCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, int level) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(level);
      });
   }

   public static record Conditions(Optional player, NumberRange.IntRange level) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), NumberRange.IntRange.CODEC.optionalFieldOf("level", NumberRange.IntRange.ANY).forGetter(Conditions::level)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, NumberRange.IntRange level) {
         this.player = playerPredicate;
         this.level = level;
      }

      public static AdvancementCriterion create() {
         return Criteria.CONSTRUCT_BEACON.create(new Conditions(Optional.empty(), NumberRange.IntRange.ANY));
      }

      public static AdvancementCriterion level(NumberRange.IntRange level) {
         return Criteria.CONSTRUCT_BEACON.create(new Conditions(Optional.empty(), level));
      }

      public boolean matches(int level) {
         return this.level.test(level);
      }

      public Optional player() {
         return this.player;
      }

      public NumberRange.IntRange level() {
         return this.level;
      }
   }
}
