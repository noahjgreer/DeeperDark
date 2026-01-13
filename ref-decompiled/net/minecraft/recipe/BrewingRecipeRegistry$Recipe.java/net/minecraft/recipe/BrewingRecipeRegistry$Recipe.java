/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;

static final class BrewingRecipeRegistry.Recipe<T>
extends Record {
    final RegistryEntry<T> from;
    final Ingredient ingredient;
    final RegistryEntry<T> to;

    BrewingRecipeRegistry.Recipe(RegistryEntry<T> from, Ingredient ingredient, RegistryEntry<T> to) {
        this.from = from;
        this.ingredient = ingredient;
        this.to = to;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BrewingRecipeRegistry.Recipe.class, "from;ingredient;to", "from", "ingredient", "to"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BrewingRecipeRegistry.Recipe.class, "from;ingredient;to", "from", "ingredient", "to"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BrewingRecipeRegistry.Recipe.class, "from;ingredient;to", "from", "ingredient", "to"}, this, object);
    }

    public RegistryEntry<T> from() {
        return this.from;
    }

    public Ingredient ingredient() {
        return this.ingredient;
    }

    public RegistryEntry<T> to() {
        return this.to;
    }
}
