/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.attribute;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public static final class EntityAttributeInstance.Packed
extends Record {
    private final RegistryEntry<EntityAttribute> attribute;
    final double baseValue;
    final List<EntityAttributeModifier> modifiers;
    public static final Codec<EntityAttributeInstance.Packed> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registries.ATTRIBUTE.getEntryCodec().fieldOf("id").forGetter(EntityAttributeInstance.Packed::attribute), (App)Codec.DOUBLE.fieldOf("base").orElse((Object)0.0).forGetter(EntityAttributeInstance.Packed::baseValue), (App)EntityAttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(EntityAttributeInstance.Packed::modifiers)).apply((Applicative)instance, EntityAttributeInstance.Packed::new));
    public static final Codec<List<EntityAttributeInstance.Packed>> LIST_CODEC = CODEC.listOf();

    public EntityAttributeInstance.Packed(RegistryEntry<EntityAttribute> attribute, double baseValue, List<EntityAttributeModifier> modifiers) {
        this.attribute = attribute;
        this.baseValue = baseValue;
        this.modifiers = modifiers;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntityAttributeInstance.Packed.class, "attribute;baseValue;modifiers", "attribute", "baseValue", "modifiers"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntityAttributeInstance.Packed.class, "attribute;baseValue;modifiers", "attribute", "baseValue", "modifiers"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntityAttributeInstance.Packed.class, "attribute;baseValue;modifiers", "attribute", "baseValue", "modifiers"}, this, object);
    }

    public RegistryEntry<EntityAttribute> attribute() {
        return this.attribute;
    }

    public double baseValue() {
        return this.baseValue;
    }

    public List<EntityAttributeModifier> modifiers() {
        return this.modifiers;
    }
}
