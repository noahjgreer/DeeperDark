package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class LightningStrikeCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return LightningStrikeCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, LightningEntity lightning, List bystanders) {
      List list = (List)bystanders.stream().map((bystander) -> {
         return EntityPredicate.createAdvancementEntityLootContext(player, bystander);
      }).collect(Collectors.toList());
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, lightning);
      this.trigger(player, (conditions) -> {
         return conditions.test(lootContext, list);
      });
   }

   public static record Conditions(Optional player, Optional lightning, Optional bystander) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("lightning").forGetter(Conditions::lightning), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("bystander").forGetter(Conditions::bystander)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional lightning, Optional bystander) {
         this.player = playerPredicate;
         this.lightning = lightning;
         this.bystander = bystander;
      }

      public static AdvancementCriterion create(Optional lightning, Optional bystander) {
         return Criteria.LIGHTNING_STRIKE.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(lightning), EntityPredicate.contextPredicateFromEntityPredicate(bystander)));
      }

      public boolean test(LootContext lightning, List bystanders) {
         if (this.lightning.isPresent() && !((LootContextPredicate)this.lightning.get()).test(lightning)) {
            return false;
         } else {
            if (this.bystander.isPresent()) {
               Stream var10000 = bystanders.stream();
               LootContextPredicate var10001 = (LootContextPredicate)this.bystander.get();
               Objects.requireNonNull(var10001);
               if (var10000.noneMatch(var10001::test)) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.lightning, "lightning");
         validator.validateEntityPredicate(this.bystander, "bystander");
      }

      public Optional player() {
         return this.player;
      }

      public Optional lightning() {
         return this.lightning;
      }

      public Optional bystander() {
         return this.bystander;
      }
   }
}
