/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public static class EntityScoresLootCondition.Builder
implements LootCondition.Builder {
    private final ImmutableMap.Builder<String, BoundedIntUnaryOperator> scores = ImmutableMap.builder();
    private final LootContext.EntityReference target;

    public EntityScoresLootCondition.Builder(LootContext.EntityReference target) {
        this.target = target;
    }

    public EntityScoresLootCondition.Builder score(String name, BoundedIntUnaryOperator value) {
        this.scores.put((Object)name, (Object)value);
        return this;
    }

    @Override
    public LootCondition build() {
        return new EntityScoresLootCondition((Map<String, BoundedIntUnaryOperator>)this.scores.build(), this.target);
    }
}
