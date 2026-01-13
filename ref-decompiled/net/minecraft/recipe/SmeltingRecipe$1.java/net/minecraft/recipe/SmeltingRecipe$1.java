/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.book.CookingRecipeCategory;

static class SmeltingRecipe.1 {
    static final /* synthetic */ int[] field_54657;

    static {
        field_54657 = new int[CookingRecipeCategory.values().length];
        try {
            SmeltingRecipe.1.field_54657[CookingRecipeCategory.BLOCKS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SmeltingRecipe.1.field_54657[CookingRecipeCategory.FOOD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SmeltingRecipe.1.field_54657[CookingRecipeCategory.MISC.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
