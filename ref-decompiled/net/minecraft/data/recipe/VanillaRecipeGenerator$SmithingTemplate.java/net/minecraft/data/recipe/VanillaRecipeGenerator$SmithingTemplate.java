/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;

public record VanillaRecipeGenerator.SmithingTemplate(Item template, RegistryKey<ArmorTrimPattern> patternId, RegistryKey<Recipe<?>> recipeId) {
}
