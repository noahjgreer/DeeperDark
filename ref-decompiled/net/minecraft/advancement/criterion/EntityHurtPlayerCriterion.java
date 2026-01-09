package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityHurtPlayerCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return EntityHurtPlayerCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(player, source, dealt, taken, blocked);
      });
   }

   public static record Conditions(Optional player, Optional damage) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(Conditions::damage)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional damage) {
         this.player = playerPredicate;
         this.damage = damage;
      }

      public static AdvancementCriterion create() {
         return Criteria.ENTITY_HURT_PLAYER.create(new Conditions(Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion create(DamagePredicate predicate) {
         return Criteria.ENTITY_HURT_PLAYER.create(new Conditions(Optional.empty(), Optional.of(predicate)));
      }

      public static AdvancementCriterion create(DamagePredicate.Builder damageBuilder) {
         return Criteria.ENTITY_HURT_PLAYER.create(new Conditions(Optional.empty(), Optional.of(damageBuilder.build())));
      }

      public boolean matches(ServerPlayerEntity player, DamageSource damageSource, float dealt, float taken, boolean blocked) {
         return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked);
      }

      public Optional player() {
         return this.player;
      }

      public Optional damage() {
         return this.damage;
      }
   }
}
