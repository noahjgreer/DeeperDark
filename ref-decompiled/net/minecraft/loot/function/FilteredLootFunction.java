package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.ErrorReporter;

public class FilteredLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((lootFunction) -> {
         return lootFunction.itemFilter;
      }), LootFunctionTypes.CODEC.fieldOf("modifier").forGetter((lootFunction) -> {
         return lootFunction.modifier;
      }))).apply(instance, FilteredLootFunction::new);
   });
   private final ItemPredicate itemFilter;
   private final LootFunction modifier;

   private FilteredLootFunction(List conditions, ItemPredicate itemFilter, LootFunction modifier) {
      super(conditions);
      this.itemFilter = itemFilter;
      this.modifier = modifier;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.FILTERED;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      return this.itemFilter.test(stack) ? (ItemStack)this.modifier.apply(stack, context) : stack;
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);
      this.modifier.validate(reporter.makeChild(new ErrorReporter.MapElementContext("modifier")));
   }
}
