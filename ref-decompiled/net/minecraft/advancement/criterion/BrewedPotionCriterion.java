package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewedPotionCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return BrewedPotionCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, RegistryEntry potion) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(potion);
      });
   }

   public static record Conditions(Optional player, Optional potion) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), Potion.CODEC.optionalFieldOf("potion").forGetter(Conditions::potion)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional optional) {
         this.player = playerPredicate;
         this.potion = optional;
      }

      public static AdvancementCriterion any() {
         return Criteria.BREWED_POTION.create(new Conditions(Optional.empty(), Optional.empty()));
      }

      public boolean matches(RegistryEntry potion) {
         return !this.potion.isPresent() || ((RegistryEntry)this.potion.get()).equals(potion);
      }

      public Optional player() {
         return this.player;
      }

      public Optional potion() {
         return this.potion;
      }
   }
}
