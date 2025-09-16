package net.noahsarch.deeperdark.potion;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CustomBrewingRecipeHandler {
    public static void register() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            // Register brewing recipe: Mundane Potion + Echo Shard = Scentless Potion
            builder.registerPotionRecipe(
                    Registries.POTION.getEntry(Identifier.of("minecraft:mundane")).orElseThrow(),
                    Ingredient.ofItems(Items.ECHO_SHARD),
                    ScentlessPotion.SCENTLESS  // Use the constant from ScentlessPotion
            );

            // Register brewing recipe: Scentless Potion + Redstone = Long Scentless Potion
            builder.registerPotionRecipe(
                    ScentlessPotion.SCENTLESS,  // Use the constant from ScentlessPotion
                    Ingredient.ofItems(Items.REDSTONE),
                    ScentlessPotion.SCENTLESS_LONG  // Use the constant from ScentlessPotion
            );
        });
    }
}
