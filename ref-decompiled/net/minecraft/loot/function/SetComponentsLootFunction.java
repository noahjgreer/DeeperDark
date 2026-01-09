package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public class SetComponentsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(ComponentChanges.CODEC.fieldOf("components").forGetter((function) -> {
         return function.changes;
      })).apply(instance, SetComponentsLootFunction::new);
   });
   private final ComponentChanges changes;

   private SetComponentsLootFunction(List conditions, ComponentChanges changes) {
      super(conditions);
      this.changes = changes;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_COMPONENTS;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      stack.applyChanges(this.changes);
      return stack;
   }

   public static ConditionalLootFunction.Builder builder(ComponentType componentType, Object value) {
      return builder((conditions) -> {
         return new SetComponentsLootFunction(conditions, ComponentChanges.builder().add(componentType, value).build());
      });
   }
}
