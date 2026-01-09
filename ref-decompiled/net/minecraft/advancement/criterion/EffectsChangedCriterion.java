package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class EffectsChangedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return EffectsChangedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, @Nullable Entity source) {
      LootContext lootContext = source != null ? EntityPredicate.createAdvancementEntityLootContext(player, source) : null;
      this.trigger(player, (conditions) -> {
         return conditions.matches(player, lootContext);
      });
   }

   public static record Conditions(Optional player, Optional effects, Optional source) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityEffectPredicate.CODEC.optionalFieldOf("effects").forGetter(Conditions::effects), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("source").forGetter(Conditions::source)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional effects, Optional source) {
         this.player = playerPredicate;
         this.effects = effects;
         this.source = source;
      }

      public static AdvancementCriterion create(EntityEffectPredicate.Builder effects) {
         return Criteria.EFFECTS_CHANGED.create(new Conditions(Optional.empty(), effects.build(), Optional.empty()));
      }

      public static AdvancementCriterion create(EntityPredicate.Builder source) {
         return Criteria.EFFECTS_CHANGED.create(new Conditions(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.asLootContextPredicate(source.build()))));
      }

      public boolean matches(ServerPlayerEntity player, @Nullable LootContext context) {
         if (this.effects.isPresent() && !((EntityEffectPredicate)this.effects.get()).test((LivingEntity)player)) {
            return false;
         } else {
            return !this.source.isPresent() || context != null && ((LootContextPredicate)this.source.get()).test(context);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.source, "source");
      }

      public Optional player() {
         return this.player;
      }

      public Optional effects() {
         return this.effects;
      }

      public Optional source() {
         return this.source;
      }
   }
}
