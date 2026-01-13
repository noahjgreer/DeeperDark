/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LootCondition;

@FunctionalInterface
public static interface LootCondition.Builder {
    public LootCondition build();

    default public LootCondition.Builder invert() {
        return InvertedLootCondition.builder(this);
    }

    default public AnyOfLootCondition.Builder or(LootCondition.Builder condition) {
        return AnyOfLootCondition.builder(this, condition);
    }

    default public AllOfLootCondition.Builder and(LootCondition.Builder condition) {
        return AllOfLootCondition.builder(this, condition);
    }
}
