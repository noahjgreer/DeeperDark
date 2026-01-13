/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder
 */
package net.minecraft.recipe;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;

public static class BrewingRecipeRegistry.Builder
implements FabricBrewingRecipeRegistryBuilder {
    private final List<Ingredient> potionTypes = new ArrayList<Ingredient>();
    private final List<BrewingRecipeRegistry.Recipe<Potion>> potionRecipes = new ArrayList<BrewingRecipeRegistry.Recipe<Potion>>();
    private final List<BrewingRecipeRegistry.Recipe<Item>> itemRecipes = new ArrayList<BrewingRecipeRegistry.Recipe<Item>>();
    private final FeatureSet enabledFeatures;

    public BrewingRecipeRegistry.Builder(FeatureSet enabledFeatures) {
        this.enabledFeatures = enabledFeatures;
    }

    private static void assertPotion(Item potionType) {
        if (!(potionType instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + String.valueOf(Registries.ITEM.getId(potionType)));
        }
    }

    public void registerItemRecipe(Item input, Item ingredient, Item output) {
        if (!(input.isEnabled(this.enabledFeatures) && ingredient.isEnabled(this.enabledFeatures) && output.isEnabled(this.enabledFeatures))) {
            return;
        }
        BrewingRecipeRegistry.Builder.assertPotion(input);
        BrewingRecipeRegistry.Builder.assertPotion(output);
        this.itemRecipes.add(new BrewingRecipeRegistry.Recipe<Item>(input.getRegistryEntry(), Ingredient.ofItem(ingredient), output.getRegistryEntry()));
    }

    public void registerPotionType(Item item) {
        if (!item.isEnabled(this.enabledFeatures)) {
            return;
        }
        BrewingRecipeRegistry.Builder.assertPotion(item);
        this.potionTypes.add(Ingredient.ofItem(item));
    }

    public void registerPotionRecipe(RegistryEntry<Potion> input, Item ingredient, RegistryEntry<Potion> output) {
        if (input.value().isEnabled(this.enabledFeatures) && ingredient.isEnabled(this.enabledFeatures) && output.value().isEnabled(this.enabledFeatures)) {
            this.potionRecipes.add(new BrewingRecipeRegistry.Recipe<Potion>(input, Ingredient.ofItem(ingredient), output));
        }
    }

    public void registerRecipes(Item ingredient, RegistryEntry<Potion> potion) {
        if (potion.value().isEnabled(this.enabledFeatures)) {
            this.registerPotionRecipe(Potions.WATER, ingredient, Potions.MUNDANE);
            this.registerPotionRecipe(Potions.AWKWARD, ingredient, potion);
        }
    }

    public BrewingRecipeRegistry build() {
        return new BrewingRecipeRegistry(List.copyOf(this.potionTypes), List.copyOf(this.potionRecipes), List.copyOf(this.itemRecipes));
    }
}
