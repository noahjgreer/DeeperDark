package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class VillagerTradeCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return VillagerTradeCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, merchant);
      this.trigger(player, (conditions) -> {
         return conditions.matches(lootContext, stack);
      });
   }

   public static record Conditions(Optional player, Optional villager, Optional item) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(Conditions::villager), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional villager, Optional item) {
         this.player = playerPredicate;
         this.villager = villager;
         this.item = item;
      }

      public static AdvancementCriterion any() {
         return Criteria.VILLAGER_TRADE.create(new Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion create(EntityPredicate.Builder playerPredicate) {
         return Criteria.VILLAGER_TRADE.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(playerPredicate)), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext villager, ItemStack stack) {
         if (this.villager.isPresent() && !((LootContextPredicate)this.villager.get()).test(villager)) {
            return false;
         } else {
            return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.villager, "villager");
      }

      public Optional player() {
         return this.player;
      }

      public Optional villager() {
         return this.villager;
      }

      public Optional item() {
         return this.item;
      }
   }
}
