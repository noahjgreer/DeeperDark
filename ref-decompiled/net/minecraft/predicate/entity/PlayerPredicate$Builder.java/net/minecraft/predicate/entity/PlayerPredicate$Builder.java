/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.InputPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameModeList;

public static class PlayerPredicate.Builder {
    private NumberRange.IntRange experienceLevel = NumberRange.IntRange.ANY;
    private GameModeList gameMode = GameModeList.ALL;
    private final ImmutableList.Builder<PlayerPredicate.StatMatcher<?>> stats = ImmutableList.builder();
    private final Object2BooleanMap<RegistryKey<Recipe<?>>> recipes = new Object2BooleanOpenHashMap();
    private final Map<Identifier, PlayerPredicate.AdvancementPredicate> advancements = Maps.newHashMap();
    private Optional<EntityPredicate> lookingAt = Optional.empty();
    private Optional<InputPredicate> input = Optional.empty();

    public static PlayerPredicate.Builder create() {
        return new PlayerPredicate.Builder();
    }

    public PlayerPredicate.Builder experienceLevel(NumberRange.IntRange experienceLevel) {
        this.experienceLevel = experienceLevel;
        return this;
    }

    public <T> PlayerPredicate.Builder stat(StatType<T> statType, RegistryEntry.Reference<T> value, NumberRange.IntRange range) {
        this.stats.add(new PlayerPredicate.StatMatcher<T>(statType, value, range));
        return this;
    }

    public PlayerPredicate.Builder recipe(RegistryKey<Recipe<?>> recipeKey, boolean unlocked) {
        this.recipes.put(recipeKey, unlocked);
        return this;
    }

    public PlayerPredicate.Builder gameMode(GameModeList gameMode) {
        this.gameMode = gameMode;
        return this;
    }

    public PlayerPredicate.Builder lookingAt(EntityPredicate.Builder lookingAt) {
        this.lookingAt = Optional.of(lookingAt.build());
        return this;
    }

    public PlayerPredicate.Builder advancement(Identifier id, boolean done) {
        this.advancements.put(id, new PlayerPredicate.CompletedAdvancementPredicate(done));
        return this;
    }

    public PlayerPredicate.Builder advancement(Identifier id, Map<String, Boolean> criteria) {
        this.advancements.put(id, new PlayerPredicate.AdvancementCriteriaPredicate((Object2BooleanMap<String>)new Object2BooleanOpenHashMap(criteria)));
        return this;
    }

    public PlayerPredicate.Builder input(InputPredicate input) {
        this.input = Optional.of(input);
        return this;
    }

    public PlayerPredicate build() {
        return new PlayerPredicate(this.experienceLevel, this.gameMode, (List<PlayerPredicate.StatMatcher<?>>)this.stats.build(), this.recipes, this.advancements, this.lookingAt, this.input);
    }
}
