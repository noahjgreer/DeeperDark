package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChangedDimensionCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ChangedDimensionCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, RegistryKey from, RegistryKey to) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(from, to);
      });
   }

   public static record Conditions(Optional player, Optional from, Optional to) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("from").forGetter(Conditions::from), RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("to").forGetter(Conditions::to)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional optional, Optional optional2) {
         this.player = playerPredicate;
         this.from = optional;
         this.to = optional2;
      }

      public static AdvancementCriterion create() {
         return Criteria.CHANGED_DIMENSION.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion create(RegistryKey from, RegistryKey to) {
         return Criteria.CHANGED_DIMENSION.create(new Conditions(Optional.empty(), Optional.of(from), Optional.of(to)));
      }

      public static AdvancementCriterion to(RegistryKey to) {
         return Criteria.CHANGED_DIMENSION.create(new Conditions(Optional.empty(), Optional.empty(), Optional.of(to)));
      }

      public static AdvancementCriterion from(RegistryKey from) {
         return Criteria.CHANGED_DIMENSION.create(new Conditions(Optional.empty(), Optional.of(from), Optional.empty()));
      }

      public boolean matches(RegistryKey from, RegistryKey to) {
         if (this.from.isPresent() && this.from.get() != from) {
            return false;
         } else {
            return !this.to.isPresent() || this.to.get() == to;
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional from() {
         return this.from;
      }

      public Optional to() {
         return this.to;
      }
   }
}
