package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public class ToggleTooltipsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(Codec.unboundedMap(ComponentType.CODEC, Codec.BOOL).fieldOf("toggles").forGetter((lootFunction) -> {
         return lootFunction.toggles;
      })).apply(instance, ToggleTooltipsLootFunction::new);
   });
   private final Map toggles;

   private ToggleTooltipsLootFunction(List conditions, Map toggles) {
      super(conditions);
      this.toggles = toggles;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT, (tooltipDisplayComponent) -> {
         Map.Entry entry;
         boolean bl;
         for(Iterator var2 = this.toggles.entrySet().iterator(); var2.hasNext(); tooltipDisplayComponent = tooltipDisplayComponent.with((ComponentType)entry.getKey(), !bl)) {
            entry = (Map.Entry)var2.next();
            bl = (Boolean)entry.getValue();
         }

         return tooltipDisplayComponent;
      });
      return stack;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.TOGGLE_TOOLTIPS;
   }
}
