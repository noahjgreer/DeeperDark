/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.registry.RegistryKey;

@FunctionalInterface
public static interface ServerRecipeBook.DisplayCollector {
    public void displaysForRecipe(RegistryKey<Recipe<?>> var1, Consumer<RecipeDisplayEntry> var2);
}
