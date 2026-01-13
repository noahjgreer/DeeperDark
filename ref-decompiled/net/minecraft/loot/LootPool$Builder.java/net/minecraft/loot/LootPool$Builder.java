/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;

public static class LootPool.Builder
implements LootFunctionConsumingBuilder<LootPool.Builder>,
LootConditionConsumingBuilder<LootPool.Builder>,
FabricLootPoolBuilder {
    private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
    private final ImmutableList.Builder<LootCondition> conditions = ImmutableList.builder();
    private final ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();
    private LootNumberProvider rolls = ConstantLootNumberProvider.create(1.0f);
    private LootNumberProvider bonusRollsRange = ConstantLootNumberProvider.create(0.0f);

    public LootPool.Builder rolls(LootNumberProvider rolls) {
        this.rolls = rolls;
        return this;
    }

    @Override
    public LootPool.Builder getThisFunctionConsumingBuilder() {
        return this;
    }

    public LootPool.Builder bonusRolls(LootNumberProvider bonusRolls) {
        this.bonusRollsRange = bonusRolls;
        return this;
    }

    public LootPool.Builder with(LootPoolEntry.Builder<?> entry) {
        this.entries.add((Object)entry.build());
        return this;
    }

    @Override
    public LootPool.Builder conditionally(LootCondition.Builder builder) {
        this.conditions.add((Object)builder.build());
        return this;
    }

    @Override
    public LootPool.Builder apply(LootFunction.Builder builder) {
        this.functions.add((Object)builder.build());
        return this;
    }

    public LootPool build() {
        return new LootPool((List<LootPoolEntry>)this.entries.build(), (List<LootCondition>)this.conditions.build(), (List<LootFunction>)this.functions.build(), this.rolls, this.bonusRollsRange);
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
        return this.getThisFunctionConsumingBuilder();
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder apply(LootFunction.Builder function) {
        return this.apply(function);
    }

    @Override
    public /* synthetic */ LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
        return this.getThisFunctionConsumingBuilder();
    }

    @Override
    public /* synthetic */ LootConditionConsumingBuilder conditionally(LootCondition.Builder condition) {
        return this.conditionally(condition);
    }
}
