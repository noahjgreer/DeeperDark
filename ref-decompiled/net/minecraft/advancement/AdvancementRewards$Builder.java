/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.advancement;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.function.LazyContainer;
import net.minecraft.util.Identifier;

public static class AdvancementRewards.Builder {
    private int experience;
    private final ImmutableList.Builder<RegistryKey<LootTable>> loot = ImmutableList.builder();
    private final ImmutableList.Builder<RegistryKey<Recipe<?>>> recipes = ImmutableList.builder();
    private Optional<Identifier> function = Optional.empty();

    public static AdvancementRewards.Builder experience(int experience) {
        return new AdvancementRewards.Builder().setExperience(experience);
    }

    public AdvancementRewards.Builder setExperience(int experience) {
        this.experience += experience;
        return this;
    }

    public static AdvancementRewards.Builder loot(RegistryKey<LootTable> loot) {
        return new AdvancementRewards.Builder().addLoot(loot);
    }

    public AdvancementRewards.Builder addLoot(RegistryKey<LootTable> loot) {
        this.loot.add(loot);
        return this;
    }

    public static AdvancementRewards.Builder recipe(RegistryKey<Recipe<?>> recipeKey) {
        return new AdvancementRewards.Builder().addRecipe(recipeKey);
    }

    public AdvancementRewards.Builder addRecipe(RegistryKey<Recipe<?>> recipeKey) {
        this.recipes.add(recipeKey);
        return this;
    }

    public static AdvancementRewards.Builder function(Identifier function) {
        return new AdvancementRewards.Builder().setFunction(function);
    }

    public AdvancementRewards.Builder setFunction(Identifier function) {
        this.function = Optional.of(function);
        return this;
    }

    public AdvancementRewards build() {
        return new AdvancementRewards(this.experience, (List<RegistryKey<LootTable>>)this.loot.build(), (List<RegistryKey<Recipe<?>>>)this.recipes.build(), this.function.map(LazyContainer::new));
    }
}
