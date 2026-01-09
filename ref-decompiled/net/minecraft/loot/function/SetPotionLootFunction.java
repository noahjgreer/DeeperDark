package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public class SetPotionLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(Potion.CODEC.fieldOf("id").forGetter((function) -> {
         return function.potion;
      })).apply(instance, SetPotionLootFunction::new);
   });
   private final RegistryEntry potion;

   private SetPotionLootFunction(List conditions, RegistryEntry potion) {
      super(conditions);
      this.potion = potion;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_POTION;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT, this.potion, PotionContentsComponent::with);
      return stack;
   }

   public static ConditionalLootFunction.Builder builder(RegistryEntry potion) {
      return builder((conditions) -> {
         return new SetPotionLootFunction(conditions, potion);
      });
   }
}
