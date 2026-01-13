/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

public static class AttributeModifiersComponent.Builder {
    private final ImmutableList.Builder<AttributeModifiersComponent.Entry> entries = ImmutableList.builder();

    AttributeModifiersComponent.Builder() {
    }

    public AttributeModifiersComponent.Builder add(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
        this.entries.add((Object)new AttributeModifiersComponent.Entry(attribute, modifier, slot));
        return this;
    }

    public AttributeModifiersComponent.Builder add(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot, AttributeModifiersComponent.Display display) {
        this.entries.add((Object)new AttributeModifiersComponent.Entry(attribute, modifier, slot, display));
        return this;
    }

    public AttributeModifiersComponent build() {
        return new AttributeModifiersComponent((List<AttributeModifiersComponent.Entry>)this.entries.build());
    }
}
