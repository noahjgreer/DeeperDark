/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;

public record RecipeUnlockedCriterion.Conditions(Optional<LootContextPredicate> player, RegistryKey<Recipe<?>> recipe) implements AbstractCriterion.Conditions
{
    public static final Codec<RecipeUnlockedCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(RecipeUnlockedCriterion.Conditions::player), (App)Recipe.KEY_CODEC.fieldOf("recipe").forGetter(RecipeUnlockedCriterion.Conditions::recipe)).apply((Applicative)instance, RecipeUnlockedCriterion.Conditions::new));

    public boolean matches(RecipeEntry<?> recipe) {
        return this.recipe == recipe.id();
    }
}
