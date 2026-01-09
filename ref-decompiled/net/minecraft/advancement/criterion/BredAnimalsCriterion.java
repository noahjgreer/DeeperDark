package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class BredAnimalsCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return BredAnimalsCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, parent);
      LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, partner);
      LootContext lootContext3 = child != null ? EntityPredicate.createAdvancementEntityLootContext(player, child) : null;
      this.trigger(player, (conditions) -> {
         return conditions.matches(lootContext, lootContext2, lootContext3);
      });
   }

   public static record Conditions(Optional player, Optional parent, Optional partner, Optional child) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("parent").forGetter(Conditions::parent), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("partner").forGetter(Conditions::partner), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("child").forGetter(Conditions::child)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional parentPredicate, Optional partnerPredicate, Optional childPredicate) {
         this.player = playerPredicate;
         this.parent = parentPredicate;
         this.partner = partnerPredicate;
         this.child = childPredicate;
      }

      public static AdvancementCriterion any() {
         return Criteria.BRED_ANIMALS.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion create(EntityPredicate.Builder child) {
         return Criteria.BRED_ANIMALS.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(child))));
      }

      public static AdvancementCriterion create(Optional parent, Optional partner, Optional child) {
         return Criteria.BRED_ANIMALS.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(parent), EntityPredicate.contextPredicateFromEntityPredicate(partner), EntityPredicate.contextPredicateFromEntityPredicate(child)));
      }

      public boolean matches(LootContext parentContext, LootContext partnerContext, @Nullable LootContext childContext) {
         if (!this.child.isPresent() || childContext != null && ((LootContextPredicate)this.child.get()).test(childContext)) {
            return parentMatches(this.parent, parentContext) && parentMatches(this.partner, partnerContext) || parentMatches(this.parent, partnerContext) && parentMatches(this.partner, parentContext);
         } else {
            return false;
         }
      }

      private static boolean parentMatches(Optional parent, LootContext parentContext) {
         return parent.isEmpty() || ((LootContextPredicate)parent.get()).test(parentContext);
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.parent, "parent");
         validator.validateEntityPredicate(this.partner, "partner");
         validator.validateEntityPredicate(this.child, "child");
      }

      public Optional player() {
         return this.player;
      }

      public Optional parent() {
         return this.parent;
      }

      public Optional partner() {
         return this.partner;
      }

      public Optional child() {
         return this.child;
      }
   }
}
