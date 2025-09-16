package net.noahsarch.deeperdark.potion;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;

public class CustomBrewingRecipeHandler {
    public static void register() {
        // Register specific recipes for creating scentless potions
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            // Register specific recipes for each base potion type to avoid conflicts
            System.out.println("DEBUG - Registering brewing recipes");

            // Water + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.WATER,                          // Water bottle base
                    Ingredient.ofItems(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.WATER                           // Output will be replaced by mixin
            );

            // Mundane + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.MUNDANE,                        // Mundane potion base
                    Ingredient.ofItems(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.MUNDANE                         // Output will be replaced by mixin
            );

            // Awkward + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.AWKWARD,                        // Awkward potion base
                    Ingredient.ofItems(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.AWKWARD                         // Output will be replaced by mixin
            );

            // The redstone extension is handled by the mixin directly, not registered here
        });
    }
}
