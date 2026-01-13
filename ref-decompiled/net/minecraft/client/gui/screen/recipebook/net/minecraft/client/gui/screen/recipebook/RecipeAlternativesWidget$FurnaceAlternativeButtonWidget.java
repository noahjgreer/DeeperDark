/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
class RecipeAlternativesWidget.FurnaceAlternativeButtonWidget
extends RecipeAlternativesWidget.AlternativeButtonWidget {
    private static final Identifier FURNACE_OVERLAY = Identifier.ofVanilla("recipe_book/furnace_overlay");
    private static final Identifier FURNACE_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_highlighted");
    private static final Identifier FURNACE_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled");
    private static final Identifier FURNACE_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled_highlighted");

    public RecipeAlternativesWidget.FurnaceAlternativeButtonWidget(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
        super(recipeAlternativesWidget, x, y, recipeId, craftable, RecipeAlternativesWidget.FurnaceAlternativeButtonWidget.alignRecipe(display, context));
    }

    private static List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> alignRecipe(RecipeDisplay display, ContextParameterMap context) {
        FurnaceRecipeDisplay furnaceRecipeDisplay;
        List<ItemStack> list;
        if (display instanceof FurnaceRecipeDisplay && !(list = (furnaceRecipeDisplay = (FurnaceRecipeDisplay)display).ingredient().getStacks(context)).isEmpty()) {
            return List.of(RecipeAlternativesWidget.FurnaceAlternativeButtonWidget.slot(1, 1, list));
        }
        return List.of();
    }

    @Override
    protected Identifier getOverlayTexture(boolean enabled) {
        if (enabled) {
            return this.isSelected() ? FURNACE_OVERLAY_HIGHLIGHTED : FURNACE_OVERLAY;
        }
        return this.isSelected() ? FURNACE_OVERLAY_DISABLED_HIGHLIGHTED : FURNACE_OVERLAY_DISABLED;
    }
}
