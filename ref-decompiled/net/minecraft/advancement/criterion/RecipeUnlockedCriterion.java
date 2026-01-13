/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.AdvancementCriterion
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.advancement.criterion.CriterionConditions
 *  net.minecraft.advancement.criterion.RecipeUnlockedCriterion
 *  net.minecraft.advancement.criterion.RecipeUnlockedCriterion$Conditions
 *  net.minecraft.recipe.Recipe
 *  net.minecraft.recipe.RecipeEntry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeUnlockedCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RecipeEntry<?> recipe) {
        this.trigger(player, conditions -> conditions.matches(recipe));
    }

    public static AdvancementCriterion<Conditions> create(RegistryKey<Recipe<?>> registryKey) {
        return Criteria.RECIPE_UNLOCKED.create((CriterionConditions)new Conditions(Optional.empty(), registryKey));
    }
}

