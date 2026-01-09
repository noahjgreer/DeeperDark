package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.ContainerComponentModifiers;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;

public class ModifyContentsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(ContainerComponentModifiers.MODIFIER_CODEC.fieldOf("component").forGetter((lootFunction) -> {
         return lootFunction.component;
      }), LootFunctionTypes.CODEC.fieldOf("modifier").forGetter((lootFunction) -> {
         return lootFunction.modifier;
      }))).apply(instance, ModifyContentsLootFunction::new);
   });
   private final ContainerComponentModifier component;
   private final LootFunction modifier;

   private ModifyContentsLootFunction(List conditions, ContainerComponentModifier component, LootFunction modifier) {
      super(conditions);
      this.component = component;
      this.modifier = modifier;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.MODIFY_CONTENTS;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         this.component.apply(stack, (content) -> {
            return (ItemStack)this.modifier.apply(content, context);
         });
         return stack;
      }
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);
      this.modifier.validate(reporter.makeChild(new ErrorReporter.MapElementContext("modifier")));
   }
}
