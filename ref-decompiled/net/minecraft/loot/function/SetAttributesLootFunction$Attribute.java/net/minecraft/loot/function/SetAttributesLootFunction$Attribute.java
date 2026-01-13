/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

static final class SetAttributesLootFunction.Attribute
extends Record {
    final Identifier id;
    final RegistryEntry<EntityAttribute> attribute;
    final EntityAttributeModifier.Operation operation;
    final LootNumberProvider amount;
    final List<AttributeModifierSlot> slots;
    private static final Codec<List<AttributeModifierSlot>> EQUIPMENT_SLOT_LIST_CODEC = Codecs.nonEmptyList(Codecs.listOrSingle(AttributeModifierSlot.CODEC));
    public static final Codec<SetAttributesLootFunction.Attribute> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(SetAttributesLootFunction.Attribute::id), (App)EntityAttribute.CODEC.fieldOf("attribute").forGetter(SetAttributesLootFunction.Attribute::attribute), (App)EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesLootFunction.Attribute::operation), (App)LootNumberProviderTypes.CODEC.fieldOf("amount").forGetter(SetAttributesLootFunction.Attribute::amount), (App)EQUIPMENT_SLOT_LIST_CODEC.fieldOf("slot").forGetter(SetAttributesLootFunction.Attribute::slots)).apply((Applicative)instance, SetAttributesLootFunction.Attribute::new));

    SetAttributesLootFunction.Attribute(Identifier id, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier.Operation operation, LootNumberProvider amount, List<AttributeModifierSlot> slots) {
        this.id = id;
        this.attribute = attribute;
        this.operation = operation;
        this.amount = amount;
        this.slots = slots;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SetAttributesLootFunction.Attribute.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SetAttributesLootFunction.Attribute.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SetAttributesLootFunction.Attribute.class, "id;attribute;operation;amount;slots", "id", "attribute", "operation", "amount", "slots"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public RegistryEntry<EntityAttribute> attribute() {
        return this.attribute;
    }

    public EntityAttributeModifier.Operation operation() {
        return this.operation;
    }

    public LootNumberProvider amount() {
        return this.amount;
    }

    public List<AttributeModifierSlot> slots() {
        return this.slots;
    }
}
