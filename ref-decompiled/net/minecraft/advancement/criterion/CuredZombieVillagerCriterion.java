package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class CuredZombieVillagerCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return CuredZombieVillagerCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, zombie);
      LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext(player, villager);
      this.trigger(player, (conditions) -> {
         return conditions.matches(lootContext, lootContext2);
      });
   }

   public static record Conditions(Optional player, Optional zombie, Optional villager) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("zombie").forGetter(Conditions::zombie), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(Conditions::villager)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional zombie, Optional villager) {
         this.player = playerPredicate;
         this.zombie = zombie;
         this.villager = villager;
      }

      public static AdvancementCriterion any() {
         return Criteria.CURED_ZOMBIE_VILLAGER.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext zombie, LootContext villager) {
         if (this.zombie.isPresent() && !((LootContextPredicate)this.zombie.get()).test(zombie)) {
            return false;
         } else {
            return !this.villager.isPresent() || ((LootContextPredicate)this.villager.get()).test(villager);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.zombie, "zombie");
         validator.validateEntityPredicate(this.villager, "villager");
      }

      public Optional player() {
         return this.player;
      }

      public Optional zombie() {
         return this.zombie;
      }

      public Optional villager() {
         return this.villager;
      }
   }
}
