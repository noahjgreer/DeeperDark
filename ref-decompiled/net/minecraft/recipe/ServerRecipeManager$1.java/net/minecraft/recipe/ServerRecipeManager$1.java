/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.recipe;

import java.util.Optional;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

static class ServerRecipeManager.1
implements ServerRecipeManager.MatchGetter<I, T> {
    private @Nullable RegistryKey<Recipe<?>> id;
    final /* synthetic */ RecipeType field_38220;

    ServerRecipeManager.1(RecipeType recipeType) {
        this.field_38220 = recipeType;
    }

    @Override
    public Optional<RecipeEntry<T>> getFirstMatch(I input, ServerWorld world) {
        ServerRecipeManager serverRecipeManager = world.getRecipeManager();
        Optional optional = serverRecipeManager.getFirstMatch(this.field_38220, input, (World)world, this.id);
        if (optional.isPresent()) {
            RecipeEntry recipeEntry = optional.get();
            this.id = recipeEntry.id();
            return Optional.of(recipeEntry);
        }
        return Optional.empty();
    }
}
