/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import java.util.Optional;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FilteredLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.predicate.item.ItemPredicate;

public static class FilteredLootFunction.Builder
extends ConditionalLootFunction.Builder<FilteredLootFunction.Builder> {
    private final ItemPredicate itemFilter;
    private Optional<LootFunction> onPass = Optional.empty();
    private Optional<LootFunction> onFail = Optional.empty();

    FilteredLootFunction.Builder(ItemPredicate itemFilter) {
        this.itemFilter = itemFilter;
    }

    @Override
    protected FilteredLootFunction.Builder getThisBuilder() {
        return this;
    }

    public FilteredLootFunction.Builder onPass(Optional<LootFunction> onPass) {
        this.onPass = onPass;
        return this;
    }

    public FilteredLootFunction.Builder onFail(Optional<LootFunction> onFail) {
        this.onFail = onFail;
        return this;
    }

    @Override
    public LootFunction build() {
        return new FilteredLootFunction(this.getConditions(), this.itemFilter, this.onPass, this.onFail);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
