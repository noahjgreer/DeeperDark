/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public static class TimeCheckLootCondition.Builder
implements LootCondition.Builder {
    private Optional<Long> period = Optional.empty();
    private final BoundedIntUnaryOperator value;

    public TimeCheckLootCondition.Builder(BoundedIntUnaryOperator value) {
        this.value = value;
    }

    public TimeCheckLootCondition.Builder period(long period) {
        this.period = Optional.of(period);
        return this;
    }

    @Override
    public TimeCheckLootCondition build() {
        return new TimeCheckLootCondition(this.period, this.value);
    }

    @Override
    public /* synthetic */ LootCondition build() {
        return this.build();
    }
}
