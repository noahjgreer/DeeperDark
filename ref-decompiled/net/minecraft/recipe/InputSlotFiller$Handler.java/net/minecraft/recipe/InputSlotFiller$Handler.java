/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;

public static interface InputSlotFiller.Handler<T extends Recipe<?>> {
    public void populateRecipeFinder(RecipeFinder var1);

    public void clear();

    public boolean matches(RecipeEntry<T> var1);
}
