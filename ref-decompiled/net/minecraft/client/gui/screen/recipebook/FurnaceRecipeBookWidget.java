/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.recipebook.FurnaceRecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.GhostRecipe
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget$Tab
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.recipe.RecipeFinder
 *  net.minecraft.recipe.display.FurnaceRecipeDisplay
 *  net.minecraft.recipe.display.RecipeDisplay
 *  net.minecraft.screen.AbstractFurnaceScreenHandler
 *  net.minecraft.screen.AbstractRecipeScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
public class FurnaceRecipeBookWidget
extends RecipeBookWidget<AbstractFurnaceScreenHandler> {
    private static final ButtonTextures FILTER_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"recipe_book/furnace_filter_enabled"), Identifier.ofVanilla((String)"recipe_book/furnace_filter_disabled"), Identifier.ofVanilla((String)"recipe_book/furnace_filter_enabled_highlighted"), Identifier.ofVanilla((String)"recipe_book/furnace_filter_disabled_highlighted"));
    private final Text toggleCraftableButtonText;

    public FurnaceRecipeBookWidget(AbstractFurnaceScreenHandler screenHandler, Text toggleCraftableButtonText, List<RecipeBookWidget.Tab> tabs) {
        super((AbstractRecipeScreenHandler)screenHandler, tabs);
        this.toggleCraftableButtonText = toggleCraftableButtonText;
    }

    protected ButtonTextures getBookButtonTextures() {
        return FILTER_BUTTON_TEXTURES;
    }

    protected boolean isCraftingSlot(Slot slot) {
        return switch (slot.id) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
        ghostRecipe.addResults(((AbstractFurnaceScreenHandler)this.craftingScreenHandler).getOutputSlot(), context, display.result());
        if (display instanceof FurnaceRecipeDisplay) {
            FurnaceRecipeDisplay furnaceRecipeDisplay = (FurnaceRecipeDisplay)display;
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
        recipeResultCollection.populateRecipes(recipeFinder, display -> display instanceof FurnaceRecipeDisplay);
    }
}

