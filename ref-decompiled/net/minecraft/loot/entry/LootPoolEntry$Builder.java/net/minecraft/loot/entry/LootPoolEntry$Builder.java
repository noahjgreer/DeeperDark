/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.SequenceEntry;

public static abstract class LootPoolEntry.Builder<T extends LootPoolEntry.Builder<T>>
implements LootConditionConsumingBuilder<T> {
    private final ImmutableList.Builder<LootCondition> conditions = ImmutableList.builder();

    protected abstract T getThisBuilder();

    @Override
    public T conditionally(LootCondition.Builder builder) {
        this.conditions.add((Object)builder.build());
        return this.getThisBuilder();
    }

    @Override
    public final T getThisConditionConsumingBuilder() {
        return this.getThisBuilder();
    }

    protected List<LootCondition> getConditions() {
        return this.conditions.build();
    }

    public AlternativeEntry.Builder alternatively(LootPoolEntry.Builder<?> builder) {
        return new AlternativeEntry.Builder(this, builder);
    }

    public GroupEntry.Builder groupEntry(LootPoolEntry.Builder<?> entry) {
        return new GroupEntry.Builder(this, entry);
    }

    public SequenceEntry.Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
        return new SequenceEntry.Builder(this, entry);
    }

    public abstract LootPoolEntry build();

    @Override
    public /* synthetic */ LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
        return this.getThisConditionConsumingBuilder();
    }

    @Override
    public /* synthetic */ LootConditionConsumingBuilder conditionally(LootCondition.Builder condition) {
        return this.conditionally(condition);
    }
}
