/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.book.CookingRecipeCategory;

static class BlastingRecipe.1 {
    static final /* synthetic */ int[] field_54630;

    static {
        field_54630 = new int[CookingRecipeCategory.values().length];
        try {
            BlastingRecipe.1.field_54630[CookingRecipeCategory.BLOCKS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlastingRecipe.1.field_54630[CookingRecipeCategory.FOOD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlastingRecipe.1.field_54630[CookingRecipeCategory.MISC.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
