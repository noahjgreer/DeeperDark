/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.input;

import net.minecraft.recipe.input.CraftingRecipeInput;

public record CraftingRecipeInput.Positioned(CraftingRecipeInput input, int left, int top) {
    public static final CraftingRecipeInput.Positioned EMPTY = new CraftingRecipeInput.Positioned(EMPTY, 0, 0);
}
