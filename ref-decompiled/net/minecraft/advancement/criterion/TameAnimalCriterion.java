package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class TameAnimalCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return TameAnimalCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
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

      public static AdvancementCriterion any() {
         return Criteria.TAME_ANIMAL.create(new Conditions(Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion create(EntityPredicate.Builder entity) {
         return Criteria.TAME_ANIMAL.create(new Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity))));
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
