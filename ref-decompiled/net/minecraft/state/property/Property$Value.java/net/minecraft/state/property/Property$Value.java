/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.state.property;

import net.minecraft.state.property.Property;

public record Property.Value<T extends Comparable<T>>(Property<T> property, T value) {
    public Property.Value {
        if (!property.getValues().contains(value)) {
            throw new IllegalArgumentException("Value " + String.valueOf(value) + " does not belong to property " + String.valueOf(property));
        }
    }

    @Override
    public String toString() {
        return this.property.getName() + "=" + this.property.name(this.value);
    }
}
