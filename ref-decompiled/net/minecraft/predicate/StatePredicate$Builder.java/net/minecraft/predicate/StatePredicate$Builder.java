/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public static class StatePredicate.Builder {
    private final ImmutableList.Builder<StatePredicate.Condition> conditions = ImmutableList.builder();

    private StatePredicate.Builder() {
    }

    public static StatePredicate.Builder create() {
        return new StatePredicate.Builder();
    }

    public StatePredicate.Builder exactMatch(Property<?> property, String valueName) {
        this.conditions.add((Object)new StatePredicate.Condition(property.getName(), new StatePredicate.ExactValueMatcher(valueName)));
        return this;
    }

    public StatePredicate.Builder exactMatch(Property<Integer> property, int value) {
        return this.exactMatch((Property)property, (Comparable<T> & StringIdentifiable)Integer.toString(value));
    }

    public StatePredicate.Builder exactMatch(Property<Boolean> property, boolean value) {
        return this.exactMatch((Property)property, (Comparable<T> & StringIdentifiable)Boolean.toString(value));
    }

    public <T extends Comparable<T> & StringIdentifiable> StatePredicate.Builder exactMatch(Property<T> property, T value) {
        return this.exactMatch(property, (T)((StringIdentifiable)value).asString());
    }

    public Optional<StatePredicate> build() {
        return Optional.of(new StatePredicate((List<StatePredicate.Condition>)this.conditions.build()));
    }
}
