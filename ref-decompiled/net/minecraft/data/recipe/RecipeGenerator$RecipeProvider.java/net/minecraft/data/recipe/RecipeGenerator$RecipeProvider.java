/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.data.recipe;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.jspecify.annotations.Nullable;

public static abstract class RecipeGenerator.RecipeProvider
implements DataProvider {
    private final DataOutput output;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

    protected RecipeGenerator.RecipeProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    @Override
    public CompletableFuture<?> run(final DataWriter writer) {
        return this.registriesFuture.thenCompose(registries -> {
            DataOutput.PathResolver pathResolver = this.output.getResolver(RegistryKeys.RECIPE);
            DataOutput.PathResolver pathResolver2 = this.output.getResolver(RegistryKeys.ADVANCEMENT);
            final HashSet set = Sets.newHashSet();
            final ArrayList list = new ArrayList();
            RecipeExporter recipeExporter = new RecipeExporter(){
                final /* synthetic */ RegistryWrapper.WrapperLookup registries;
                final /* synthetic */ DataOutput.PathResolver recipePathResolver;
                final /* synthetic */ DataOutput.PathResolver recipeAdvancementPathResolver;
                {
                    this.registries = wrapperLookup;
                    this.recipePathResolver = pathResolver;
                    this.recipeAdvancementPathResolver = pathResolver2;
                }

                @Override
                public void accept(RegistryKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                    if (!set.add(key)) {
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
                    list.add(DataProvider.writeCodecToPath(writer, this.registries, Recipe.CODEC, recipe, this.recipePathResolver.resolveJson(key.getValue())));
                }

                private void addRecipeAdvancement(AdvancementEntry advancementEntry) {
                    list.add(DataProvider.writeCodecToPath(writer, this.registries, Advancement.CODEC, advancementEntry.value(), this.recipeAdvancementPathResolver.resolveJson(advancementEntry.id())));
                }
            };
            this.getRecipeGenerator((RegistryWrapper.WrapperLookup)registries, recipeExporter).generate();
            return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
        });
    }

    protected abstract RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup var1, RecipeExporter var2);
}
