package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;

public record MatchToolLootCondition(Optional predicate) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchToolLootCondition::predicate)).apply(instance, MatchToolLootCondition::new);
   });

   public MatchToolLootCondition(Optional optional) {
      this.predicate = optional;
   }

   public LootConditionType getType() {
      return LootConditionTypes.MATCH_TOOL;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.TOOL);
   }

   public boolean test(LootContext lootContext) {
      ItemStack itemStack = (ItemStack)lootContext.get(LootContextParameters.TOOL);
      return itemStack != null && (this.predicate.isEmpty() || ((ItemPredicate)this.predicate.get()).test(itemStack));
   }

   public static LootCondition.Builder builder(ItemPredicate.Builder predicate) {
      return () -> {
         return new MatchToolLootCondition(Optional.of(predicate.build()));
      };
   }

   public Optional predicate() {
      return this.predicate;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
