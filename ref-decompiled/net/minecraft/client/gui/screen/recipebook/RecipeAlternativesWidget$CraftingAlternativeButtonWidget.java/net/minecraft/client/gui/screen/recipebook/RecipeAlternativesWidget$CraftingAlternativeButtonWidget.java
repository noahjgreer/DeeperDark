/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
class RecipeAlternativesWidget.CraftingAlternativeButtonWidget
extends RecipeAlternativesWidget.AlternativeButtonWidget {
    private static final Identifier CRAFTING_OVERLAY = Identifier.ofVanilla("recipe_book/crafting_overlay");
    private static final Identifier CRAFTING_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_highlighted");
    private static final Identifier CRAFTING_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled");
    private static final Identifier CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled_highlighted");
    private static final int field_54828 = 3;
    private static final int field_54829 = 3;

    public RecipeAlternativesWidget.CraftingAlternativeButtonWidget(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
        super(recipeAlternativesWidget, x, y, recipeId, craftable, RecipeAlternativesWidget.CraftingAlternativeButtonWidget.collectInputSlots(display, context));
    }

    private static List<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> collectInputSlots(RecipeDisplay display, ContextParameterMap context) {
        ArrayList<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot> list = new ArrayList<RecipeAlternativesWidget.AlternativeButtonWidget.InputSlot>();
        RecipeDisplay recipeDisplay = display;
        Objects.requireNonNull(recipeDisplay);
        RecipeDisplay recipeDisplay2 = recipeDisplay;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, n)) {
            case 0: {
                ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                RecipeGridAligner.alignRecipeToGrid(3, 3, shapedCraftingRecipeDisplay.width(), shapedCraftingRecipeDisplay.height(), shapedCraftingRecipeDisplay.ingredients(), (slot, index, x, y) -> {
                    List<ItemStack> list2 = slot.getStacks(context);
                    if (!list2.isEmpty()) {
                        list.add(RecipeAlternativesWidget.CraftingAlternativeButtonWidget.slot(x, y, list2));
                    }
                });
                break;
            }
            case 1: {
                ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                List<SlotDisplay> list2 = shapelessCraftingRecipeDisplay.ingredients();
                for (int i = 0; i < list2.size(); ++i) {
                    List<ItemStack> list3 = list2.get(i).getStacks(context);
                    if (list3.isEmpty()) continue;
                    list.add(RecipeAlternativesWidget.CraftingAlternativeButtonWidget.slot(i % 3, i / 3, list3));
                }
                break;
            }
        }
        return list;
    }

    @Override
    protected Identifier getOverlayTexture(boolean enabled) {
        if (enabled) {
            return this.isSelected() ? CRAFTING_OVERLAY_HIGHLIGHTED : CRAFTING_OVERLAY;
        }
        return this.isSelected() ? CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED : CRAFTING_OVERLAY_DISABLED;
    }
}
