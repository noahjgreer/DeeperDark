/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.data.recipe;

import java.util.List;
import java.util.Set;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import org.jspecify.annotations.Nullable;

class RecipeGenerator.RecipeProvider.1
implements RecipeExporter {
    final /* synthetic */ Set field_53725;
    final /* synthetic */ List field_53726;
    final /* synthetic */ DataWriter field_53727;
    final /* synthetic */ RegistryWrapper.WrapperLookup registries;
    final /* synthetic */ DataOutput.PathResolver recipePathResolver;
    final /* synthetic */ DataOutput.PathResolver recipeAdvancementPathResolver;

    RecipeGenerator.RecipeProvider.1() {
        this.field_53725 = set;
        this.field_53726 = list;
        this.field_53727 = dataWriter;
        this.registries = wrapperLookup;
        this.recipePathResolver = pathResolver;
        this.recipeAdvancementPathResolver = pathResolver2;
    }

    @Override
    public void accept(RegistryKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
        if (!this.field_53725.add(key)) {
            throw new IllegalStateException("Duplicate recipe " + String.valueOf(key.getValue()));
        }
        this.addRecipe(key, recipe);
        if (advancement != null) {
            this.addRecipeAdvancement(advancement);
        }
    }

    @Override
    public Advancement.Builder getAdvancementBuilder() {
        return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
    }

    @Override
    public void addRootAdvancement() {
        AdvancementEntry advancementEntry = Advancement.Builder.createUntelemetered().criterion("impossible", Criteria.IMPOSSIBLE.create(new ImpossibleCriterion.Conditions())).build(CraftingRecipeJsonBuilder.ROOT);
        this.addRecipeAdvancement(advancementEntry);
    }

    private void addRecipe(RegistryKey<Recipe<?>> key, Recipe<?> recipe) {
        this.field_53726.add(DataProvider.writeCodecToPath(this.field_53727, this.registries, Recipe.CODEC, recipe, this.recipePathResolver.resolveJson(key.getValue())));
    }

    private void addRecipeAdvancement(AdvancementEntry advancementEntry) {
        this.field_53726.add(DataProvider.writeCodecToPath(this.field_53727, this.registries, Advancement.CODEC, advancementEntry.value(), this.recipeAdvancementPathResolver.resolveJson(advancementEntry.id())));
    }
}
