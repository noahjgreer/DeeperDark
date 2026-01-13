/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.recipe;

import net.minecraft.recipe.book.RecipeCategory;

static class CraftingRecipeJsonBuilder.1 {
    static final /* synthetic */ int[] field_47503;

    static {
        field_47503 = new int[RecipeCategory.values().length];
        try {
            CraftingRecipeJsonBuilder.1.field_47503[RecipeCategory.BUILDING_BLOCKS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipeJsonBuilder.1.field_47503[RecipeCategory.TOOLS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipeJsonBuilder.1.field_47503[RecipeCategory.COMBAT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CraftingRecipeJsonBuilder.1.field_47503[RecipeCategory.REDSTONE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
