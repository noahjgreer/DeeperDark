package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.collection.ListOperation;
import org.jetbrains.annotations.Nullable;

public class SetLoreLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(TextCodecs.CODEC.sizeLimitedListOf(256).fieldOf("lore").forGetter((function) -> {
         return function.lore;
      }), ListOperation.createCodec(256).forGetter((function) -> {
         return function.operation;
      }), LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter((function) -> {
         return function.entity;
      }))).apply(instance, SetLoreLootFunction::new);
   });
   private final List lore;
   private final ListOperation operation;
   private final Optional entity;

   public SetLoreLootFunction(List conditions, List lore, ListOperation operation, Optional entity) {
      super(conditions);
      this.lore = List.copyOf(lore);
      this.operation = operation;
      this.entity = entity;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_LORE;
   }

   public Set getAllowedParameters() {
      return (Set)this.entity.map((entity) -> {
         return Set.of(entity.getParameter());
      }).orElseGet(Set::of);
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, (component) -> {
         return new LoreComponent(this.getNewLoreTexts(component, context));
      });
      return stack;
   }

   private List getNewLoreTexts(@Nullable LoreComponent current, LootContext context) {
      if (current == null && this.lore.isEmpty()) {
         return List.of();
      } else {
         UnaryOperator unaryOperator = SetNameLootFunction.applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse((Object)null));
         List list = this.lore.stream().map(unaryOperator).toList();
         return this.operation.apply(current.lines(), list, 256);
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private Optional target = Optional.empty();
      private final ImmutableList.Builder lore = ImmutableList.builder();
      private ListOperation operation;

      public Builder() {
         this.operation = ListOperation.Append.INSTANCE;
      }

      public Builder operation(ListOperation operation) {
         this.operation = operation;
         return this;
      }

      public Builder target(LootContext.EntityTarget target) {
         this.target = Optional.of(target);
         return this;
      }

      public Builder lore(Text lore) {
         this.lore.add(lore);
         return this;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public LootFunction build() {
         return new SetLoreLootFunction(this.getConditions(), this.lore.build(), this.operation, this.target);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
