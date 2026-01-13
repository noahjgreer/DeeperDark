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
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public record ItemCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> location) implements AbstractCriterion.Conditions
{
    public static final Codec<ItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemCriterion.Conditions::player), (App)LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(ItemCriterion.Conditions::location)).apply((Applicative)instance, ItemCriterion.Conditions::new));

    public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedBlock(Block block) {
        LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).build());
        return Criteria.PLACED_BLOCK.create(new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
    }

    public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedBlock(LootCondition.Builder ... locationConditions) {
        LootContextPredicate lootContextPredicate = LootContextPredicate.create((LootCondition[])Arrays.stream(locationConditions).map(LootCondition.Builder::build).toArray(LootCondition[]::new));
        return Criteria.PLACED_BLOCK.create(new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
    }

    public static <T extends Comparable<T>> AdvancementCriterion<ItemCriterion.Conditions> createPlacedWithState(Block block, Property<T> property, String value) {
        StatePredicate.Builder builder = StatePredicate.Builder.create().exactMatch(property, value);
        LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).properties(builder).build());
        return Criteria.PLACED_BLOCK.create(new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
    }

    public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedWithState(Block block, Property<Boolean> property, boolean value) {
        return ItemCriterion.Conditions.createPlacedWithState(block, property, String.valueOf(value));
    }

    public static AdvancementCriterion<ItemCriterion.Conditions> createPlacedWithState(Block block, Property<Integer> property, int value) {
        return ItemCriterion.Conditions.createPlacedWithState(block, property, String.valueOf(value));
    }

    public static <T extends Comparable<T> & StringIdentifiable> AdvancementCriterion<ItemCriterion.Conditions> createPlacedWithState(Block block, Property<T> property, T value) {
        return ItemCriterion.Conditions.createPlacedWithState(block, property, ((StringIdentifiable)value).asString());
    }

    private static ItemCriterion.Conditions create(LocationPredicate.Builder location, ItemPredicate.Builder item) {
        LootContextPredicate lootContextPredicate = LootContextPredicate.create(LocationCheckLootCondition.builder(location).build(), MatchToolLootCondition.builder(item).build());
        return new ItemCriterion.Conditions(Optional.empty(), Optional.of(lootContextPredicate));
    }

    public static AdvancementCriterion<ItemCriterion.Conditions> createItemUsedOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
        return Criteria.ITEM_USED_ON_BLOCK.create(ItemCriterion.Conditions.create(location, item));
    }

    public static AdvancementCriterion<ItemCriterion.Conditions> createAllayDropItemOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
        return Criteria.ALLAY_DROP_ITEM_ON_BLOCK.create(ItemCriterion.Conditions.create(location, item));
    }

    public boolean test(LootContext location) {
        return this.location.isEmpty() || this.location.get().test(location);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        this.location.ifPresent(location -> validator.validate((LootContextPredicate)location, LootContextTypes.ADVANCEMENT_LOCATION, "location"));
    }
}
