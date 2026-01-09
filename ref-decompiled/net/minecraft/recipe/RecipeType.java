package net.minecraft.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface RecipeType {
   RecipeType CRAFTING = register("crafting");
   RecipeType SMELTING = register("smelting");
   RecipeType BLASTING = register("blasting");
   RecipeType SMOKING = register("smoking");
   RecipeType CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType STONECUTTING = register("stonecutting");
   RecipeType SMITHING = register("smithing");

   static RecipeType register(final String id) {
      return (RecipeType)Registry.register(Registries.RECIPE_TYPE, (Identifier)Identifier.ofVanilla(id), new RecipeType() {
         public String toString() {
            return id;
         }
      });
   }
}
