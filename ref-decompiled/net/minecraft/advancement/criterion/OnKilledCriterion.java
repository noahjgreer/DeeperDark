package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnKilledCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return OnKilledCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Entity entity, DamageSource killingDamage) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
      this.trigger(player, (conditions) -> {
         return conditions.test(player, lootContext, killingDamage);
      });
   }

   public static record Conditions(Optional player, Optional entity, Optional killingBlow) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(Conditions::entity), DamageSourcePredicate.CODEC.optionalFieldOf("killing_blow").forGetter(Conditions::killingBlow)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional entity, Optional killingBlow) {
         this.player = playerPredicate;
         this.entity = entity;
         this.killingBlow = killingBlow;
      }

      public static AdvancementCriterion createPlayerKilledEntity(Optional entity) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()));
      }

      public static AdvancementCriterion createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), Optional.empty()));
      }

      public static AdvancementCriterion createPlayerKilledEntity() {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion createPlayerKilledEntity(Optional entity, Optional killingBlow) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow));
      }

      public static AdvancementCriterion createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder, Optional killingBlow) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), killingBlow));
      }

      public static AdvancementCriterion createPlayerKilledEntity(Optional entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.of(damageSourcePredicateBuilder.build())));
      }

      public static AdvancementCriterion createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder) {
         return Criteria.PLAYER_KILLED_ENTITY.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), Optional.of(killingBlowBuilder.build())));
      }

      public static AdvancementCriterion createKillMobNearSculkCatalyst() {
         return Criteria.KILL_MOB_NEAR_SCULK_CATALYST.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion createEntityKilledPlayer(Optional entity) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()));
      }

      public static AdvancementCriterion createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)), Optional.empty()));
      }

      public static AdvancementCriterion createEntityKilledPlayer() {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion createEntityKilledPlayer(Optional entity, Optional killingBlow) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow));
      }

      public static AdvancementCriterion createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder, Optional killingBlow) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)), killingBlow));
      }

      public static AdvancementCriterion createEntityKilledPlayer(Optional entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.of(damageSourcePredicateBuilder.build())));
      }

      public static AdvancementCriterion createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder) {
         return Criteria.ENTITY_KILLED_PLAYER.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)), Optional.of(damageSourcePredicateBuilder.build())));
      }

      public boolean test(ServerPlayerEntity player, LootContext entity, DamageSource killingBlow) {
         if (this.killingBlow.isPresent() && !((DamageSourcePredicate)this.killingBlow.get()).test(player, killingBlow)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.entity, "entity");
      }

      public Optional player() {
         return this.player;
      }

      public Optional entity() {
         return this.entity;
      }

      public Optional killingBlow() {
         return this.killingBlow;
      }
   }
}
