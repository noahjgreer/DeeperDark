/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

record StatusEffect.EffectAttributeModifierCreator(Identifier id, double baseValue, EntityAttributeModifier.Operation operation) {
    public EntityAttributeModifier createAttributeModifier(int amplifier) {
        return new EntityAttributeModifier(this.id, this.baseValue * (double)(amplifier + 1), this.operation);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatusEffect.EffectAttributeModifierCreator.class, "id;amount;operation", "id", "baseValue", "operation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatusEffect.EffectAttributeModifierCreator.class, "id;amount;operation", "id", "baseValue", "operation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatusEffect.EffectAttributeModifierCreator.class, "id;amount;operation", "id", "baseValue", "operation"}, this, object);
    }
}
