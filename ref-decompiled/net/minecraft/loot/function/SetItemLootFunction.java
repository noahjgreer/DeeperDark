package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.entry.RegistryEntry;

public class SetItemLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(Item.ENTRY_CODEC.fieldOf("item").forGetter((lootFunction) -> {
         return lootFunction.item;
      })).apply(instance, SetItemLootFunction::new);
   });
   private final RegistryEntry item;

   private SetItemLootFunction(List conditions, RegistryEntry item) {
      super(conditions);
      this.item = item;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_ITEM;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      return stack.withItem((ItemConvertible)this.item.value());
   }
}
