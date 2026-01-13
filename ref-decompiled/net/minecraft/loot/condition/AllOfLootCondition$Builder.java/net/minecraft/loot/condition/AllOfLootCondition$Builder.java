/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import java.util.List;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootCondition;

public static class AllOfLootCondition.Builder
extends AlternativeLootCondition.Builder {
    public AllOfLootCondition.Builder(LootCondition.Builder ... builders) {
        super(builders);
    }

    @Override
    public AllOfLootCondition.Builder and(LootCondition.Builder builder) {
        this.add(builder);
        return this;
    }

    @Override
    protected LootCondition build(List<LootCondition> terms) {
        return new AllOfLootCondition(terms);
    }
}
