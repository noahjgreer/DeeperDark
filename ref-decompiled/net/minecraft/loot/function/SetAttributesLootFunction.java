package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class SetAttributesLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(SetAttributesLootFunction.Attribute.CODEC.listOf().fieldOf("modifiers").forGetter((function) -> {
         return function.attributes;
      }), Codec.BOOL.optionalFieldOf("replace", true).forGetter((lootFunction) -> {
         return lootFunction.replace;
      }))).apply(instance, SetAttributesLootFunction::new);
   });
   private final List attributes;
   private final boolean replace;

   SetAttributesLootFunction(List conditions, List attributes, boolean replace) {
      super(conditions);
      this.attributes = List.copyOf(attributes);
      this.replace = replace;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_ATTRIBUTES;
   }

   public Set getAllowedParameters() {
      return (Set)this.attributes.stream().flatMap((attribute) -> {
         return attribute.amount.getAllowedParameters().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (this.replace) {
         stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.applyTo(context, AttributeModifiersComponent.DEFAULT));
      } else {
         stack.apply(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT, (attributeModifiersComponent) -> {
            return this.applyTo(context, attributeModifiersComponent);
         });
      }

      return stack;
   }

   private AttributeModifiersComponent applyTo(LootContext context, AttributeModifiersComponent attributeModifiersComponent) {
      Random random = context.getRandom();

      Attribute attribute;
      AttributeModifierSlot attributeModifierSlot;
      for(Iterator var4 = this.attributes.iterator(); var4.hasNext(); attributeModifiersComponent = attributeModifiersComponent.with(attribute.attribute, new EntityAttributeModifier(attribute.id, (double)attribute.amount.nextFloat(context), attribute.operation), attributeModifierSlot)) {
         attribute = (Attribute)var4.next();
         attributeModifierSlot = (AttributeModifierSlot)Util.getRandom(attribute.slots, random);
      }

      return attributeModifiersComponent;
   }

   public static AttributeBuilder attributeBuilder(Identifier id, RegistryEntry attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amountRange) {
      return new AttributeBuilder(id, attribute, operation, amountRange);
   }

   public static Builder builder() {
      return new Builder();
   }

   private static record Attribute(Identifier id, RegistryEntry attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount, List slots) {
      final Identifier id;
      final RegistryEntry attribute;
      final EntityAttributeModifier.Operation operation;
      final LootNumberProvider amount;
      final List slots;
      private static final Codec EQUIPMENT_SLOT_LIST_CODEC;
      public static final Codec CODEC;

      Attribute(Identifier identifier, RegistryEntry registryEntry, EntityAttributeModifier.Operation operation, LootNumberProvider amount, List list) {
         this.id = identifier;
         this.attribute = registryEntry;
         this.operation = operation;
         this.amount = amount;
         this.slots = list;
      }

      public Identifier id() {
         return this.id;
      }

      public RegistryEntry attribute() {
         return this.attribute;
      }

      public EntityAttributeModifier.Operation operation() {
         return this.operation;
      }

      public LootNumberProvider amount() {
         return this.amount;
      }

      public List slots() {
         return this.slots;
      }

      static {
         EQUIPMENT_SLOT_LIST_CODEC = Codecs.nonEmptyList(Codecs.listOrSingle(AttributeModifierSlot.CODEC));
         CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Identifier.CODEC.fieldOf("id").forGetter(Attribute::id), EntityAttribute.CODEC.fieldOf("attribute").forGetter(Attribute::attribute), EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Attribute::operation), LootNumberProviderTypes.CODEC.fieldOf("amount").forGetter(Attribute::amount), EQUIPMENT_SLOT_LIST_CODEC.fieldOf("slot").forGetter(Attribute::slots)).apply(instance, Attribute::new);
         });
      }
   }

   public static class AttributeBuilder {
      private final Identifier id;
      private final RegistryEntry attribute;
      private final EntityAttributeModifier.Operation operation;
      private final LootNumberProvider amount;
      private final Set slots = EnumSet.noneOf(AttributeModifierSlot.class);

      public AttributeBuilder(Identifier id, RegistryEntry attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount) {
         this.id = id;
         this.attribute = attribute;
         this.operation = operation;
         this.amount = amount;
      }

      public AttributeBuilder slot(AttributeModifierSlot slot) {
         this.slots.add(slot);
         return this;
      }

      public Attribute build() {
         return new Attribute(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
      }
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final boolean replace;
      private final List attributes;

      public Builder(boolean replace) {
         this.attributes = Lists.newArrayList();
         this.replace = replace;
      }

      public Builder() {
         this(false);
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder attribute(AttributeBuilder attribute) {
         this.attributes.add(attribute.build());
         return this;
      }

      public LootFunction build() {
         return new SetAttributesLootFunction(this.getConditions(), this.attributes, this.replace);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
