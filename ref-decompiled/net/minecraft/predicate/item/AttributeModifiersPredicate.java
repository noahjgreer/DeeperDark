package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

public record AttributeModifiersPredicate(Optional modifiers) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(AttributeModifiersPredicate.AttributeModifierPredicate.CODEC).optionalFieldOf("modifiers").forGetter(AttributeModifiersPredicate::modifiers)).apply(instance, AttributeModifiersPredicate::new);
   });

   public AttributeModifiersPredicate(Optional optional) {
      this.modifiers = optional;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.ATTRIBUTE_MODIFIERS;
   }

   public boolean test(AttributeModifiersComponent attributeModifiersComponent) {
      return !this.modifiers.isPresent() || ((CollectionPredicate)this.modifiers.get()).test((Iterable)attributeModifiersComponent.modifiers());
   }

   public Optional modifiers() {
      return this.modifiers;
   }

   public static record AttributeModifierPredicate(Optional attribute, Optional id, NumberRange.DoubleRange amount, Optional operation, Optional slot) implements Predicate {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RegistryCodecs.entryList(RegistryKeys.ATTRIBUTE).optionalFieldOf("attribute").forGetter(AttributeModifierPredicate::attribute), Identifier.CODEC.optionalFieldOf("id").forGetter(AttributeModifierPredicate::id), NumberRange.DoubleRange.CODEC.optionalFieldOf("amount", NumberRange.DoubleRange.ANY).forGetter(AttributeModifierPredicate::amount), EntityAttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(AttributeModifierPredicate::operation), AttributeModifierSlot.CODEC.optionalFieldOf("slot").forGetter(AttributeModifierPredicate::slot)).apply(instance, AttributeModifierPredicate::new);
      });

      public AttributeModifierPredicate(Optional optional, Optional optional2, NumberRange.DoubleRange doubleRange, Optional optional3, Optional optional4) {
         this.attribute = optional;
         this.id = optional2;
         this.amount = doubleRange;
         this.operation = optional3;
         this.slot = optional4;
      }

      public boolean test(AttributeModifiersComponent.Entry entry) {
         if (this.attribute.isPresent() && !((RegistryEntryList)this.attribute.get()).contains(entry.attribute())) {
            return false;
         } else if (this.id.isPresent() && !((Identifier)this.id.get()).equals(entry.modifier().id())) {
            return false;
         } else if (!this.amount.test(entry.modifier().value())) {
            return false;
         } else if (this.operation.isPresent() && this.operation.get() != entry.modifier().operation()) {
            return false;
         } else {
            return !this.slot.isPresent() || this.slot.get() == entry.slot();
         }
      }

      public Optional attribute() {
         return this.attribute;
      }

      public Optional id() {
         return this.id;
      }

      public NumberRange.DoubleRange amount() {
         return this.amount;
      }

      public Optional operation() {
         return this.operation;
      }

      public Optional slot() {
         return this.slot;
      }

      // $FF: synthetic method
      public boolean test(final Object attributeModifier) {
         return this.test((AttributeModifiersComponent.Entry)attributeModifier);
      }
   }
}
