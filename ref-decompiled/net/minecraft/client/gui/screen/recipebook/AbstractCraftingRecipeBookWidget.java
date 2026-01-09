package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(EnvType.CLIENT)
public class AbstractCraftingRecipeBookWidget extends RecipeBookWidget {
   private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/filter_enabled"), Identifier.ofVanilla("recipe_book/filter_disabled"), Identifier.ofVanilla("recipe_book/filter_enabled_highlighted"), Identifier.ofVanilla("recipe_book/filter_disabled_highlighted"));
   private static final Text TOGGLE_CRAFTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.craftable");
   private static final List TABS;

   public AbstractCraftingRecipeBookWidget(AbstractCraftingScreenHandler screenHandler) {
      super(screenHandler, TABS);
   }

   protected boolean isValid(Slot slot) {
      return ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getOutputSlot() == slot || ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots().contains(slot);
   }

   private boolean canDisplay(RecipeDisplay display) {
      int i = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getWidth();
      int j = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getHeight();
      Objects.requireNonNull(display);
      byte var5 = 0;
      boolean var10000;
      switch (display.typeSwitch<invokedynamic>(display, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)display;
            var10000 = i >= shapedCraftingRecipeDisplay.width() && j >= shapedCraftingRecipeDisplay.height();
            break;
         case 1:
            ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)display;
            var10000 = i * j >= shapelessCraftingRecipeDisplay.ingredients().size();
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
      ghostRecipe.addResults(((AbstractCraftingScreenHandler)this.craftingScreenHandler).getOutputSlot(), context, display.result());
      Objects.requireNonNull(display);
      byte var5 = 0;
      switch (display.typeSwitch<invokedynamic>(display, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)display;
            List list = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots();
            RecipeGridAligner.alignRecipeToGrid(((AbstractCraftingScreenHandler)this.craftingScreenHandler).getWidth(), ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getHeight(), shapedCraftingRecipeDisplay.width(), shapedCraftingRecipeDisplay.height(), shapedCraftingRecipeDisplay.ingredients(), (slot, index, x, y) -> {
               Slot slot2 = (Slot)list.get(index);
               ghostRecipe.addInputs(slot2, context, slot);
            });
            break;
         case 1:
            ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)display;
            List list2 = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots();
            int i = Math.min(shapelessCraftingRecipeDisplay.ingredients().size(), list2.size());

            for(int j = 0; j < i; ++j) {
               ghostRecipe.addInputs((Slot)list2.get(j), context, (SlotDisplay)shapelessCraftingRecipeDisplay.ingredients().get(j));
            }
      }

   }

   protected void setBookButtonTexture() {
      this.toggleCraftableButton.setTextures(TEXTURES);
   }

   protected Text getToggleCraftableButtonText() {
      return TOGGLE_CRAFTABLE_TEXT;
   }

   protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
      recipeResultCollection.populateRecipes(recipeFinder, this::canDisplay);
   }

   static {
      TABS = List.of(new RecipeBookWidget.Tab(RecipeBookType.CRAFTING), new RecipeBookWidget.Tab(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT), new RecipeBookWidget.Tab(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS), new RecipeBookWidget.Tab(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC), new RecipeBookWidget.Tab(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE));
   }
}
