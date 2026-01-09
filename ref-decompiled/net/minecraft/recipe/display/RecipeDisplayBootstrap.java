package net.minecraft.recipe.display;

import net.minecraft.registry.Registry;

public class RecipeDisplayBootstrap {
   public static RecipeDisplay.Serializer registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"crafting_shapeless", ShapelessCraftingRecipeDisplay.SERIALIZER);
      Registry.register(registry, (String)"crafting_shaped", ShapedCraftingRecipeDisplay.SERIALIZER);
      Registry.register(registry, (String)"furnace", FurnaceRecipeDisplay.SERIALIZER);
      Registry.register(registry, (String)"stonecutter", StonecutterRecipeDisplay.SERIALIZER);
      return (RecipeDisplay.Serializer)Registry.register(registry, (String)"smithing", SmithingRecipeDisplay.SERIALIZER);
   }
}
