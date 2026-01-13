/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.RecipeCraftedCriterion
 *  net.minecraft.advancement.criterion.RecipeCraftedCriterion$Conditions
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.Recipe
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeCraftedCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RegistryKey<Recipe<?>> recipeKey, List<ItemStack> ingredients) {
        this.trigger(player, conditions -> conditions.matches(recipeKey, ingredients));
    }
}

