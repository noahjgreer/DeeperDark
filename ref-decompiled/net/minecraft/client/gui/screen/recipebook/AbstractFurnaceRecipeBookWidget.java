package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(EnvType.CLIENT)
public class AbstractFurnaceRecipeBookWidget extends RecipeBookWidget {
   private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/furnace_filter_enabled"), Identifier.ofVanilla("recipe_book/furnace_filter_disabled"), Identifier.ofVanilla("recipe_book/furnace_filter_enabled_highlighted"), Identifier.ofVanilla("recipe_book/furnace_filter_disabled_highlighted"));
   private final Text toggleCraftableButtonText;

   public AbstractFurnaceRecipeBookWidget(AbstractFurnaceScreenHandler screenHandler, Text toggleCraftableButtonText, List tabs) {
      super(screenHandler, tabs);
      this.toggleCraftableButtonText = toggleCraftableButtonText;
   }

   protected void setBookButtonTexture() {
      this.toggleCraftableButton.setTextures(TEXTURES);
   }

   protected boolean isValid(Slot slot) {
      boolean var10000;
      switch (slot.id) {
         case 0:
         case 1:
         case 2:
            var10000 = true;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
      ghostRecipe.addResults(((AbstractFurnaceScreenHandler)this.craftingScreenHandler).getOutputSlot(), context, display.result());
      if (display instanceof FurnaceRecipeDisplay furnaceRecipeDisplay) {
         ghostRecipe.addInputs((Slot)((AbstractFurnaceScreenHandler)this.craftingScreenHandler).slots.get(0), context, furnaceRecipeDisplay.ingredient());
         Slot slot = (Slot)((AbstractFurnaceScreenHandler)this.craftingScreenHandler).slots.get(1);
         if (slot.getStack().isEmpty()) {
            ghostRecipe.addInputs(slot, context, furnaceRecipeDisplay.fuel());
         }
      }

   }

   protected Text getToggleCraftableButtonText() {
      return this.toggleCraftableButtonText;
   }

   protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
      recipeResultCollection.populateRecipes(recipeFinder, (display) -> {
         return display instanceof FurnaceRecipeDisplay;
      });
   }
}
