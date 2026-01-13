/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;

public static class ServerRecipeManager.PropertySetBuilder
implements Consumer<Recipe<?>> {
    final RegistryKey<RecipePropertySet> propertySetKey;
    private final ServerRecipeManager.SoleIngredientGetter ingredientGetter;
    private final List<Ingredient> ingredients = new ArrayList<Ingredient>();

    protected ServerRecipeManager.PropertySetBuilder(RegistryKey<RecipePropertySet> propertySetKey, ServerRecipeManager.SoleIngredientGetter ingredientGetter) {
        this.propertySetKey = propertySetKey;
        this.ingredientGetter = ingredientGetter;
    }

    @Override
    public void accept(Recipe<?> recipe) {
        this.ingredientGetter.apply(recipe).ifPresent(this.ingredients::add);
    }

    public RecipePropertySet build(FeatureSet enabledFeatures) {
        return RecipePropertySet.of(ServerRecipeManager.filterIngredients(enabledFeatures, this.ingredients));
    }

    @Override
    public /* synthetic */ void accept(Object recipe) {
        this.accept((Recipe)recipe);
    }
}
