/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public static final class AttributeModifiersComponent.Entry
extends Record {
    final RegistryEntry<EntityAttribute> attribute;
    final EntityAttributeModifier modifier;
    final AttributeModifierSlot slot;
    final AttributeModifiersComponent.Display display;
    public static final Codec<AttributeModifiersComponent.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityAttribute.CODEC.fieldOf("type").forGetter(AttributeModifiersComponent.Entry::attribute), (App)EntityAttributeModifier.MAP_CODEC.forGetter(AttributeModifiersComponent.Entry::modifier), (App)AttributeModifierSlot.CODEC.optionalFieldOf("slot", (Object)AttributeModifierSlot.ANY).forGetter(AttributeModifiersComponent.Entry::slot), (App)AttributeModifiersComponent.Display.CODEC.optionalFieldOf("display", (Object)AttributeModifiersComponent.Display.Default.INSTANCE).forGetter(AttributeModifiersComponent.Entry::display)).apply((Applicative)instance, AttributeModifiersComponent.Entry::new));
    public static final PacketCodec<RegistryByteBuf, AttributeModifiersComponent.Entry> PACKET_CODEC = PacketCodec.tuple(EntityAttribute.PACKET_CODEC, AttributeModifiersComponent.Entry::attribute, EntityAttributeModifier.PACKET_CODEC, AttributeModifiersComponent.Entry::modifier, AttributeModifierSlot.PACKET_CODEC, AttributeModifiersComponent.Entry::slot, AttributeModifiersComponent.Display.PACKET_CODEC, AttributeModifiersComponent.Entry::display, AttributeModifiersComponent.Entry::new);

    public AttributeModifiersComponent.Entry(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
        this(attribute, modifier, slot, AttributeModifiersComponent.Display.getDefault());
    }

    public AttributeModifiersComponent.Entry(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot, AttributeModifiersComponent.Display display) {
        this.attribute = attribute;
        this.modifier = modifier;
        this.slot = slot;
        this.display = display;
    }

    public boolean matches(RegistryEntry<EntityAttribute> attribute, Identifier modifierId) {
        return attribute.equals(this.attribute) && this.modifier.idMatches(modifierId);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{AttributeModifiersComponent.Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{AttributeModifiersComponent.Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{AttributeModifiersComponent.Entry.class, "attribute;modifier;slot;display", "attribute", "modifier", "slot", "display"}, this, object);
    }

    public RegistryEntry<EntityAttribute> attribute() {
        return this.attribute;
    }

    public EntityAttributeModifier modifier() {
        return this.modifier;
    }

    public AttributeModifierSlot slot() {
        return this.slot;
    }

    public AttributeModifiersComponent.Display display() {
        return this.display;
    }
}
