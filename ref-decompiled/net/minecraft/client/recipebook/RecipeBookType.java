package net.minecraft.client.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(EnvType.CLIENT)
public enum RecipeBookType implements RecipeBookGroup {
   CRAFTING(new RecipeBookCategory[]{RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE}),
   FURNACE(new RecipeBookCategory[]{RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC}),
   BLAST_FURNACE(new RecipeBookCategory[]{RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC}),
   SMOKER(new RecipeBookCategory[]{RecipeBookCategories.SMOKER_FOOD});

   private final List categories;

   private RecipeBookType(final RecipeBookCategory... categories) {
      this.categories = List.of(categories);
   }

   public List getCategories() {
      return this.categories;
   }

   // $FF: synthetic method
   private static RecipeBookType[] method_64889() {
      return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
   }
}
