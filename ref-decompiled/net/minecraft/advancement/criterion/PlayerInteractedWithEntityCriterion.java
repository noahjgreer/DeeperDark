package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerInteractedWithEntityCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return PlayerInteractedWithEntityCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack stack, Entity entity) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
      this.trigger(player, (conditions) -> {
         return conditions.test(stack, lootContext);
      });
   }

   public static record Conditions(Optional player, Optional item, Optional entity) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(Conditions::entity)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional item, Optional entity) {
         this.player = playerPredicate;
         this.item = item;
         this.entity = entity;
      }

      public static AdvancementCriterion create(Optional playerPredicate, ItemPredicate.Builder item, Optional entity) {
         return Criteria.PLAYER_INTERACTED_WITH_ENTITY.create(new Conditions(playerPredicate, Optional.of(item.build()), entity));
      }

      public static AdvancementCriterion createPlayerShearedEquipment(Optional playerPredicate, ItemPredicate.Builder item, Optional entity) {
         return Criteria.PLAYER_SHEARED_EQUIPMENT.create(new Conditions(playerPredicate, Optional.of(item.build()), entity));
      }

      public static AdvancementCriterion createPlayerShearedEquipment(ItemPredicate.Builder item, Optional entity) {
         return Criteria.PLAYER_SHEARED_EQUIPMENT.create(new Conditions(Optional.empty(), Optional.of(item.build()), entity));
      }

      public static AdvancementCriterion create(ItemPredicate.Builder item, Optional entity) {
         return create(Optional.empty(), item, entity);
      }

      public boolean test(ItemStack stack, LootContext entity) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)) {
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

      public Optional item() {
         return this.item;
      }

      public Optional entity() {
         return this.entity;
      }
   }
}
