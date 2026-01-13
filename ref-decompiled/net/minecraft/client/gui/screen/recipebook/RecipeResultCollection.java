/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection$RecipeFilterMode
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.recipe.RecipeDisplayEntry
 *  net.minecraft.recipe.RecipeFinder
 *  net.minecraft.recipe.display.RecipeDisplay
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.RecipeDisplay;

@Environment(value=EnvType.CLIENT)
public class RecipeResultCollection {
    public static final RecipeResultCollection EMPTY = new RecipeResultCollection(List.of());
    private final List<RecipeDisplayEntry> entries;
    private final Set<NetworkRecipeId> craftableRecipes = new HashSet();
    private final Set<NetworkRecipeId> displayableRecipes = new HashSet();

    public RecipeResultCollection(List<RecipeDisplayEntry> entries) {
        this.entries = entries;
    }

    public void populateRecipes(RecipeFinder finder, Predicate<RecipeDisplay> displayablePredicate) {
        for (RecipeDisplayEntry recipeDisplayEntry : this.entries) {
            boolean bl = displayablePredicate.test(recipeDisplayEntry.display());
            if (bl) {
                this.displayableRecipes.add(recipeDisplayEntry.id());
            } else {
                this.displayableRecipes.remove(recipeDisplayEntry.id());
            }
            if (bl && recipeDisplayEntry.isCraftable(finder)) {
                this.craftableRecipes.add(recipeDisplayEntry.id());
                continue;
            }
            this.craftableRecipes.remove(recipeDisplayEntry.id());
        }
    }

    public boolean isCraftable(NetworkRecipeId recipeId) {
        return this.craftableRecipes.contains(recipeId);
    }

    public boolean hasCraftableRecipes() {
        return !this.craftableRecipes.isEmpty();
    }

    public boolean hasDisplayableRecipes() {
        return !this.displayableRecipes.isEmpty();
    }

    public List<RecipeDisplayEntry> getAllRecipes() {
        return this.entries;
    }

    public List<RecipeDisplayEntry> filter(RecipeFilterMode filterMode) {
        Predicate<NetworkRecipeId> predicate = switch (filterMode.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.displayableRecipes::contains;
            case 1 -> this.craftableRecipes::contains;
            case 2 -> recipeId -> this.displayableRecipes.contains(recipeId) && !this.craftableRecipes.contains(recipeId);
        };
        ArrayList<RecipeDisplayEntry> list = new ArrayList<RecipeDisplayEntry>();
        for (RecipeDisplayEntry recipeDisplayEntry : this.entries) {
            if (!predicate.test(recipeDisplayEntry.id())) continue;
            list.add(recipeDisplayEntry);
        }
        return list;
    }
}

