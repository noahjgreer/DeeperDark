package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHurtEntityCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return PlayerHurtEntityCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Entity entity, DamageSource damage, float dealt, float taken, boolean blocked) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
      this.trigger(player, (conditions) -> {
         return conditions.matches(player, lootContext, damage, dealt, taken, blocked);
      });
   }

   public static record Conditions(Optional player, Optional damage, Optional entity) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(Conditions::damage), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(Conditions::entity)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional damage, Optional entity) {
         this.player = playerPredicate;
         this.damage = damage;
         this.entity = entity;
      }

      public static AdvancementCriterion create() {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion createDamage(Optional damage) {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), damage, Optional.empty()));
      }

      public static AdvancementCriterion create(DamagePredicate.Builder damage) {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), Optional.of(damage.build()), Optional.empty()));
      }

      public static AdvancementCriterion createEntity(Optional entity) {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity)));
      }

      public static AdvancementCriterion create(Optional damage, Optional entity) {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), damage, EntityPredicate.contextPredicateFromEntityPredicate(entity)));
      }

      public static AdvancementCriterion create(DamagePredicate.Builder damage, Optional entity) {
         return Criteria.PLAYER_HURT_ENTITY.create(new Conditions(Optional.empty(), Optional.of(damage.build()), EntityPredicate.contextPredicateFromEntityPredicate(entity)));
      }

      public boolean matches(ServerPlayerEntity player, LootContext entity, DamageSource damageSource, float dealt, float taken, boolean blocked) {
         if (this.damage.isPresent() && !((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked)) {
            return false;
         } else {
            return !this.entity.isPresent() || ((LootContextPredicate)this.entity.get()).test(entity);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.entity, "entity");
      }

      public Optional player() {
         return this.player;
      }

      public Optional damage() {
         return this.damage;
      }

      public Optional entity() {
         return this.entity;
      }
   }
}
