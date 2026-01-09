package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return TargetHitCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Entity projectile, Vec3d hitPos, int signalStrength) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, projectile);
      this.trigger(player, (conditions) -> {
         return conditions.test(lootContext, hitPos, signalStrength);
      });
   }

   public static record Conditions(Optional player, NumberRange.IntRange signalStrength, Optional projectile) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), NumberRange.IntRange.CODEC.optionalFieldOf("signal_strength", NumberRange.IntRange.ANY).forGetter(Conditions::signalStrength), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("projectile").forGetter(Conditions::projectile)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, NumberRange.IntRange signalStrength, Optional projectile) {
         this.player = playerPredicate;
         this.signalStrength = signalStrength;
         this.projectile = projectile;
      }

      public static AdvancementCriterion create(NumberRange.IntRange signalStrength, Optional projectile) {
         return Criteria.TARGET_HIT.create(new Conditions(Optional.empty(), signalStrength, projectile));
      }

      public boolean test(LootContext projectile, Vec3d hitPos, int signalStrength) {
         if (!this.signalStrength.test(signalStrength)) {
            return false;
         } else {
            return !this.projectile.isPresent() || ((LootContextPredicate)this.projectile.get()).test(projectile);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.projectile, "projectile");
      }

      public Optional player() {
         return this.player;
      }

      public NumberRange.IntRange signalStrength() {
         return this.signalStrength;
      }

      public Optional projectile() {
         return this.projectile;
      }
   }
}
