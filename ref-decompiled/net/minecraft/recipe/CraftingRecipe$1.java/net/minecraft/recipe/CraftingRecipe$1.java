/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.book.CraftingRecipeCategory;

static class CraftingRecipe.1 {
    static final /* synthetic */ int[] field_54634;

    static {
        field_54634 = new int[CraftingRecipeCategory.values().length];
        try {
            CraftingRecipe.1.field_54634[CraftingRecipeCategory.BUILDING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipe.1.field_54634[CraftingRecipeCategory.EQUIPMENT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipe.1.field_54634[CraftingRecipeCategory.REDSTONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipe.1.field_54634[CraftingRecipeCategory.MISC.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
