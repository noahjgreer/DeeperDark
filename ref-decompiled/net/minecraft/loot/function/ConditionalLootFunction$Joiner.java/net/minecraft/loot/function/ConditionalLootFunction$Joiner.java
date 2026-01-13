/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import java.util.List;
import java.util.function.Function;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;

static final class ConditionalLootFunction.Joiner
extends ConditionalLootFunction.Builder<ConditionalLootFunction.Joiner> {
    private final Function<List<LootCondition>, LootFunction> joiner;

    public ConditionalLootFunction.Joiner(Function<List<LootCondition>, LootFunction> joiner) {
        this.joiner = joiner;
    }

    @Override
    protected ConditionalLootFunction.Joiner getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return this.joiner.apply(this.getConditions());
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
