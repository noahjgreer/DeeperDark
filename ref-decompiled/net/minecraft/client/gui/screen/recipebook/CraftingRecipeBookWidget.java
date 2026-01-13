/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.recipebook.CraftingRecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.GhostRecipe
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget$Tab
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.recipebook.RecipeBookType
 *  net.minecraft.item.Items
 *  net.minecraft.recipe.RecipeFinder
 *  net.minecraft.recipe.RecipeGridAligner
 *  net.minecraft.recipe.book.RecipeBookCategories
 *  net.minecraft.recipe.display.RecipeDisplay
 *  net.minecraft.recipe.display.ShapedCraftingRecipeDisplay
 *  net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay
 *  net.minecraft.recipe.display.SlotDisplay
 *  net.minecraft.screen.AbstractCraftingScreenHandler
 *  net.minecraft.screen.AbstractRecipeScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 */
package net.minecraft.client.gui.screen.recipebook;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
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
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
public class CraftingRecipeBookWidget
extends RecipeBookWidget<AbstractCraftingScreenHandler> {
    private static final ButtonTextures FILTER_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"recipe_book/filter_enabled"), Identifier.ofVanilla((String)"recipe_book/filter_disabled"), Identifier.ofVanilla((String)"recipe_book/filter_enabled_highlighted"), Identifier.ofVanilla((String)"recipe_book/filter_disabled_highlighted"));
    private static final Text TOGGLE_CRAFTABLE_TEXT = Text.translatable((String)"gui.recipebook.toggleRecipes.craftable");
    private static final List<RecipeBookWidget.Tab> TABS = List.of(new RecipeBookWidget.Tab(RecipeBookType.CRAFTING), new RecipeBookWidget.Tab(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT), new RecipeBookWidget.Tab(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS), new RecipeBookWidget.Tab(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC), new RecipeBookWidget.Tab(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE));

    public CraftingRecipeBookWidget(AbstractCraftingScreenHandler screenHandler) {
        super((AbstractRecipeScreenHandler)screenHandler, TABS);
    }

    protected boolean isCraftingSlot(Slot slot) {
        return ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getOutputSlot() == slot || ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots().contains(slot);
    }

    private boolean canDisplay(RecipeDisplay display) {
        int i = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getWidth();
        int j = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getHeight();
        RecipeDisplay recipeDisplay = display;
        Objects.requireNonNull(recipeDisplay);
        RecipeDisplay recipeDisplay2 = recipeDisplay;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, n)) {
            case 0 -> {
                ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                if (i >= shapedCraftingRecipeDisplay.width() && j >= shapedCraftingRecipeDisplay.height()) {
                    yield true;
                }
                yield false;
            }
            case 1 -> {
                ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                if (i * j >= shapelessCraftingRecipeDisplay.ingredients().size()) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
        ghostRecipe.addResults(((AbstractCraftingScreenHandler)this.craftingScreenHandler).getOutputSlot(), context, display.result());
        RecipeDisplay recipeDisplay = display;
        Objects.requireNonNull(recipeDisplay);
        RecipeDisplay recipeDisplay2 = recipeDisplay;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, n)) {
            case 0: {
                ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                List list = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots();
                RecipeGridAligner.alignRecipeToGrid((int)((AbstractCraftingScreenHandler)this.craftingScreenHandler).getWidth(), (int)((AbstractCraftingScreenHandler)this.craftingScreenHandler).getHeight(), (int)shapedCraftingRecipeDisplay.width(), (int)shapedCraftingRecipeDisplay.height(), (Iterable)shapedCraftingRecipeDisplay.ingredients(), (slot, index, x, y) -> {
                    Slot slot2 = (Slot)list.get(index);
                    ghostRecipe.addInputs(slot2, context, slot);
                });
                break;
            }
            case 1: {
                ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                List list2 = ((AbstractCraftingScreenHandler)this.craftingScreenHandler).getInputSlots();
                int i = Math.min(shapelessCraftingRecipeDisplay.ingredients().size(), list2.size());
                for (int j = 0; j < i; ++j) {
                    ghostRecipe.addInputs((Slot)list2.get(j), context, (SlotDisplay)shapelessCraftingRecipeDisplay.ingredients().get(j));
                }
                break;
            }
        }
    }

    protected ButtonTextures getBookButtonTextures() {
        return FILTER_BUTTON_TEXTURES;
    }

    protected Text getToggleCraftableButtonText() {
        return TOGGLE_CRAFTABLE_TEXT;
    }

    protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
        recipeResultCollection.populateRecipes(recipeFinder, arg_0 -> this.canDisplay(arg_0));
    }
}

