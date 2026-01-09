package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ItemDurabilityChangedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ItemDurabilityChangedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack stack, int durability) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(stack, durability);
      });
   }

   public static record Conditions(Optional player, Optional item, NumberRange.IntRange durability, NumberRange.IntRange delta) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item), NumberRange.IntRange.CODEC.optionalFieldOf("durability", NumberRange.IntRange.ANY).forGetter(Conditions::durability), NumberRange.IntRange.CODEC.optionalFieldOf("delta", NumberRange.IntRange.ANY).forGetter(Conditions::delta)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional item, NumberRange.IntRange durability, NumberRange.IntRange delta) {
         this.player = playerPredicate;
         this.item = item;
         this.durability = durability;
         this.delta = delta;
      }

      public static AdvancementCriterion create(Optional item, NumberRange.IntRange durability) {
         return create(Optional.empty(), item, durability);
      }

      public static AdvancementCriterion create(Optional playerPredicate, Optional item, NumberRange.IntRange durability) {
         return Criteria.ITEM_DURABILITY_CHANGED.create(new Conditions(playerPredicate, item, durability, NumberRange.IntRange.ANY));
      }

      public boolean matches(ItemStack stack, int durability) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack)) {
            return false;
         } else if (!this.durability.test(stack.getMaxDamage() - durability)) {
            return false;
         } else {
            return this.delta.test(stack.getDamage() - durability);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional item() {
         return this.item;
      }

      public NumberRange.IntRange durability() {
         return this.durability;
      }

      public NumberRange.IntRange delta() {
         return this.delta;
      }
   }
}
