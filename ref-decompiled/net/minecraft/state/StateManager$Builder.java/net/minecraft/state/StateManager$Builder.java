/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.state;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public static class StateManager.Builder<O, S extends State<O, S>> {
    private final O owner;
    private final Map<String, Property<?>> namedProperties = Maps.newHashMap();

    public StateManager.Builder(O owner) {
        this.owner = owner;
    }

    public StateManager.Builder<O, S> add(Property<?> ... properties) {
        for (Property<?> property : properties) {
            this.validate(property);
            this.namedProperties.put(property.getName(), property);
        }
        return this;
    }

    private <T extends Comparable<T>> void validate(Property<T> property) {
        String string = property.getName();
        if (!VALID_NAME_PATTERN.matcher(string).matches()) {
            throw new IllegalArgumentException(String.valueOf(this.owner) + " has invalidly named property: " + string);
        }
        List<T> collection = property.getValues();
        if (collection.size() <= 1) {
            throw new IllegalArgumentException(String.valueOf(this.owner) + " attempted use property " + string + " with <= 1 possible values");
        }
        for (Comparable comparable : collection) {
            String string2 = property.name(comparable);
            if (VALID_NAME_PATTERN.matcher(string2).matches()) continue;
            throw new IllegalArgumentException(String.valueOf(this.owner) + " has property: " + string + " with invalidly named value: " + string2);
        }
        if (this.namedProperties.containsKey(string)) {
            throw new IllegalArgumentException(String.valueOf(this.owner) + " has duplicate property: " + string);
        }
    }

    public StateManager<O, S> build(Function<O, S> defaultStateGetter, StateManager.Factory<O, S> factory) {
        return new StateManager<O, S>(defaultStateGetter, this.owner, factory, this.namedProperties);
    }
}
