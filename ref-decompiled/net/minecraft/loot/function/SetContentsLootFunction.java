package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.ContainerComponentModifiers;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ErrorReporter;

public class SetContentsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(ContainerComponentModifiers.MODIFIER_CODEC.fieldOf("component").forGetter((function) -> {
         return function.component;
      }), LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter((function) -> {
         return function.entries;
      }))).apply(instance, SetContentsLootFunction::new);
   });
   private final ContainerComponentModifier component;
   private final List entries;

   SetContentsLootFunction(List conditions, ContainerComponentModifier component, List entries) {
      super(conditions);
      this.component = component;
      this.entries = List.copyOf(entries);
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_CONTENTS;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         Stream.Builder builder = Stream.builder();
         this.entries.forEach((entry) -> {
            entry.expand(context, (choice) -> {
               ServerWorld var10001 = context.getWorld();
               Objects.requireNonNull(builder);
               choice.generateLoot(LootTable.processStacks(var10001, builder::add), context);
            });
         });
         this.component.apply(stack, builder.build());
         return stack;
      }
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);

      for(int i = 0; i < this.entries.size(); ++i) {
         ((LootPoolEntry)this.entries.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("entries", i)));
      }

   }

   public static Builder builder(ContainerComponentModifier componentModifier) {
      return new Builder(componentModifier);
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();
      private final ContainerComponentModifier componentModifier;

      public Builder(ContainerComponentModifier componentModifier) {
         this.componentModifier = componentModifier;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder withEntry(LootPoolEntry.Builder entryBuilder) {
         this.entries.add(entryBuilder.build());
         return this;
      }

      public LootFunction build() {
         return new SetContentsLootFunction(this.getConditions(), this.componentModifier, this.entries.build());
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
