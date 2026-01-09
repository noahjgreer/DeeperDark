package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class UsingItemCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return UsingItemCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack stack) {
      this.trigger(player, (conditions) -> {
         return conditions.test(stack);
      });
   }

   public static record Conditions(Optional player, Optional item) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional item) {
         this.player = playerPredicate;
         this.item = item;
      }

      public static AdvancementCriterion create(EntityPredicate.Builder player, ItemPredicate.Builder item) {
         return Criteria.USING_ITEM.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player)), Optional.of(item.build())));
      }

      public boolean test(ItemStack stack) {
         return !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
      }

      public Optional player() {
         return this.player;
      }

      public Optional item() {
         return this.item;
      }
   }
}
