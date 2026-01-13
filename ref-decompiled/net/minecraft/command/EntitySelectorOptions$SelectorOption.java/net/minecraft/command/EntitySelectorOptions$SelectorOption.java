/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.text.Text;

static final class EntitySelectorOptions.SelectorOption
extends Record {
    final EntitySelectorOptions.SelectorHandler handler;
    final Predicate<EntitySelectorReader> condition;
    final Text description;

    EntitySelectorOptions.SelectorOption(EntitySelectorOptions.SelectorHandler handler, Predicate<EntitySelectorReader> condition, Text description) {
        this.handler = handler;
        this.condition = condition;
        this.description = description;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntitySelectorOptions.SelectorOption.class, "modifier;canUse;description", "handler", "condition", "description"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntitySelectorOptions.SelectorOption.class, "modifier;canUse;description", "handler", "condition", "description"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntitySelectorOptions.SelectorOption.class, "modifier;canUse;description", "handler", "condition", "description"}, this, object);
    }

    public EntitySelectorOptions.SelectorHandler handler() {
        return this.handler;
    }

    public Predicate<EntitySelectorReader> condition() {
        return this.condition;
    }

    public Text description() {
        return this.description;
    }
}
