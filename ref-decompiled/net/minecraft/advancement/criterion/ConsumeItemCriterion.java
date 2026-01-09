package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.network.ServerPlayerEntity;

public class ConsumeItemCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ConsumeItemCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack stack) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(stack);
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

      public static AdvancementCriterion any() {
         return Criteria.CONSUME_ITEM.create(new Conditions(Optional.empty(), Optional.empty()));
      }

      public static AdvancementCriterion item(RegistryEntryLookup itemRegistry, ItemConvertible item) {
         return predicate(ItemPredicate.Builder.create().items(itemRegistry, item.asItem()));
      }

      public static AdvancementCriterion predicate(ItemPredicate.Builder predicate) {
         return Criteria.CONSUME_ITEM.create(new Conditions(Optional.empty(), Optional.of(predicate.build())));
      }

      public boolean matches(ItemStack stack) {
         return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test(stack);
      }

      public Optional player() {
         return this.player;
      }

      public Optional item() {
         return this.item;
      }
   }
}
