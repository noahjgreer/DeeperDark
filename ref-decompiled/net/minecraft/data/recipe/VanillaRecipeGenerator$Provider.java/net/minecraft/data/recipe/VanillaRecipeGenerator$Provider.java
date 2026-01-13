/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.recipe;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.VanillaRecipeGenerator;
import net.minecraft.registry.RegistryWrapper;

public static class VanillaRecipeGenerator.Provider
extends RecipeGenerator.RecipeProvider {
    public VanillaRecipeGenerator.Provider(DataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(dataOutput, completableFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new VanillaRecipeGenerator(registries, exporter);
    }

    @Override
    public String getName() {
        return "Vanilla Recipes";
    }
}
