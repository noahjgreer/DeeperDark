/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.function.LootFunction;

public static abstract class ConditionalLootFunction.Builder<T extends ConditionalLootFunction.Builder<T>>
implements LootFunction.Builder,
LootConditionConsumingBuilder<T> {
    private final ImmutableList.Builder<LootCondition> conditionList = ImmutableList.builder();

    @Override
    public T conditionally(LootCondition.Builder builder) {
        this.conditionList.add((Object)builder.build());
        return this.getThisBuilder();
    }

    @Override
    public final T getThisConditionConsumingBuilder() {
        return this.getThisBuilder();
    }

    protected abstract T getThisBuilder();

    protected List<LootCondition> getConditions() {
        return this.conditionList.build();
    }

    @Override
    public /* synthetic */ LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
        return this.getThisConditionConsumingBuilder();
    }

    @Override
    public /* synthetic */ LootConditionConsumingBuilder conditionally(LootCondition.Builder condition) {
        return this.conditionally(condition);
    }
}
