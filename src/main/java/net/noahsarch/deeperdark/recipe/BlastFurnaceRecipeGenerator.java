package net.noahsarch.deeperdark.recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;

public class BlastFurnaceRecipeGenerator extends FabricRecipeProvider {
    public BlastFurnaceRecipeGenerator(FabricDataGenerator generator) {
        super(generator);
    }

    @Override
    public void generateRecipes(Consumer<net.minecraft.data.server.recipe.RecipeJsonProvider> exporter) {
        RegistryWrapper.WrapperLookup lookup = getRegistryLookup();
        Set<Item> foodItems = new HashSet<>();
        for (Item item : Registries.ITEM) {
            if (item.getComponents().get(DataComponentTypes.FOOD) != null) {
                foodItems.add(item);
            }
        }
        for (Recipe<?> recipe : lookup.getWrapperOrThrow(RegistryKeys.RECIPE).stream().toList()) {
            if (recipe instanceof SmeltingRecipe smeltingRecipe) {
                ItemStack result = smeltingRecipe.getOutput();
                if (!foodItems.contains(result.getItem())) {
                    // Generate a blast furnace recipe for this smelting recipe
                    Identifier id = new Identifier("deeperdark", "blast_" + smeltingRecipe.getId().getPath());
                    BlastingRecipe blastRecipe = new BlastingRecipe(
                        id,
                        smeltingRecipe.getGroup(),
                        smeltingRecipe.getCategory(),
                        smeltingRecipe.getIngredients().get(0),
                        result,
                        smeltingRecipe.getExperience(),
                        Math.max(100, smeltingRecipe.getCookingTime() / 2)
                    );
                    exporter.accept(net.minecraft.data.server.recipe.CookingRecipeJsonFactory.create(blastRecipe, RecipeSerializer.BLASTING));
                }
            }
        }
    }
}

