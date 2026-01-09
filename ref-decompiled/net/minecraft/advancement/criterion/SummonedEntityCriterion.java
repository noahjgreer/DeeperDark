package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class SummonedEntityCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return SummonedEntityCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Entity entity) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
      this.trigger(player, (conditions) -> {
         return conditions.matches(lootContext);
      });
   }

   public static record Conditions(Optional player, Optional entity) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(Conditions::entity)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional entity) {
         this.player = playerPredicate;
         this.entity = entity;
      }

      public static AdvancementCriterion create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
         return Criteria.SUMMONED_ENTITY.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(summonedEntityPredicateBuilder))));
      }

      public boolean matches(LootContext entity) {
         return this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
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
   }
}
