package net.minecraft.recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public interface RecipeUnlocker {
   void setLastRecipe(@Nullable RecipeEntry recipe);

   @Nullable
   RecipeEntry getLastRecipe();

   default void unlockLastRecipe(PlayerEntity player, List ingredients) {
      RecipeEntry recipeEntry = this.getLastRecipe();
      if (recipeEntry != null) {
         player.onRecipeCrafted(recipeEntry, ingredients);
         if (!recipeEntry.value().isIgnoredInRecipeBook()) {
            player.unlockRecipes((Collection)Collections.singleton(recipeEntry));
            this.setLastRecipe((RecipeEntry)null);
         }
      }

   }

   default boolean shouldCraftRecipe(ServerPlayerEntity player, RecipeEntry recipe) {
      if (!recipe.value().isIgnoredInRecipeBook() && player.getWorld().getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) && !player.getRecipeBook().isUnlocked(recipe.id())) {
         return false;
      } else {
         this.setLastRecipe(recipe);
         return true;
      }
   }
}
