/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public static class SetAttributesLootFunction.AttributeBuilder {
    private final Identifier id;
    private final RegistryEntry<EntityAttribute> attribute;
    private final EntityAttributeModifier.Operation operation;
    private final LootNumberProvider amount;
    private final Set<AttributeModifierSlot> slots = EnumSet.noneOf(AttributeModifierSlot.class);

    public SetAttributesLootFunction.AttributeBuilder(Identifier id, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount) {
        this.id = id;
        this.attribute = attribute;
        this.operation = operation;
        this.amount = amount;
    }

    public SetAttributesLootFunction.AttributeBuilder slot(AttributeModifierSlot slot) {
        this.slots.add(slot);
        return this;
    }

    public SetAttributesLootFunction.Attribute build() {
        return new SetAttributesLootFunction.Attribute(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
    }
}
