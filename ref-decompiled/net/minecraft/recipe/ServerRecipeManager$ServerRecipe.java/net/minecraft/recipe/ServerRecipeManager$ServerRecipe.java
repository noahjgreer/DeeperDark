/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeEntry;

public static final class ServerRecipeManager.ServerRecipe
extends Record {
    final RecipeDisplayEntry display;
    final RecipeEntry<?> parent;

    public ServerRecipeManager.ServerRecipe(RecipeDisplayEntry display, RecipeEntry<?> parent) {
        this.display = display;
        this.parent = parent;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerRecipeManager.ServerRecipe.class, "display;parent", "display", "parent"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerRecipeManager.ServerRecipe.class, "display;parent", "display", "parent"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerRecipeManager.ServerRecipe.class, "display;parent", "display", "parent"}, this, object);
    }

    public RecipeDisplayEntry display() {
        return this.display;
    }

    public RecipeEntry<?> parent() {
        return this.parent;
    }
}
