/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.state;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.state.property.Property;
import org.jspecify.annotations.Nullable;

class State.1
implements Function<Map.Entry<Property<?>, Comparable<?>>, String> {
    State.1() {
    }

    @Override
    public String apply( @Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
        if (entry == null) {
            return "<NULL>";
        }
        Property<?> property = entry.getKey();
        return property.getName() + "=" + this.nameValue(property, entry.getValue());
    }

    private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    @Override
    public /* synthetic */ Object apply(@Nullable Object entry) {
        return this.apply((Map.Entry)entry);
    }
}
