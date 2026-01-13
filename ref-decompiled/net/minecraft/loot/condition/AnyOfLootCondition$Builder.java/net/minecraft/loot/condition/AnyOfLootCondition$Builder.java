/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import java.util.List;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.LootCondition;

public static class AnyOfLootCondition.Builder
extends AlternativeLootCondition.Builder {
    public AnyOfLootCondition.Builder(LootCondition.Builder ... builders) {
        super(builders);
    }

    @Override
    public AnyOfLootCondition.Builder or(LootCondition.Builder builder) {
        this.add(builder);
        return this;
    }

    @Override
    protected LootCondition build(List<LootCondition> terms) {
        return new AnyOfLootCondition(terms);
    }
}
