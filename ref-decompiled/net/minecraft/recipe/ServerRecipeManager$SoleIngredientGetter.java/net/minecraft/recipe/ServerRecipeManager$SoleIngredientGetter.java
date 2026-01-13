/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;

@FunctionalInterface
public static interface ServerRecipeManager.SoleIngredientGetter {
    public Optional<Ingredient> apply(Recipe<?> var1);
}
