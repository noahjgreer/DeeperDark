/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;

public static interface ServerRecipeManager.MatchGetter<I extends RecipeInput, T extends Recipe<I>> {
    public Optional<RecipeEntry<T>> getFirstMatch(I var1, ServerWorld var2);
}
