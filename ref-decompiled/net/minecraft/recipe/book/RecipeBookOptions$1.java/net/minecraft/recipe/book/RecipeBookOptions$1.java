/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

import net.minecraft.recipe.book.RecipeBookType;

static class RecipeBookOptions.1 {
    static final /* synthetic */ int[] field_60340;

    static {
        field_60340 = new int[RecipeBookType.values().length];
        try {
            RecipeBookOptions.1.field_60340[RecipeBookType.CRAFTING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RecipeBookOptions.1.field_60340[RecipeBookType.FURNACE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RecipeBookOptions.1.field_60340[RecipeBookType.BLAST_FURNACE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RecipeBookOptions.1.field_60340[RecipeBookType.SMOKER.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
