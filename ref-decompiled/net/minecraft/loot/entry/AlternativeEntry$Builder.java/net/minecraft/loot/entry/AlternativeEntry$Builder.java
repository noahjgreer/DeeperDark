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
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.LootPoolEntry;

public static class AlternativeEntry.Builder
extends LootPoolEntry.Builder<AlternativeEntry.Builder> {
    private final ImmutableList.Builder<LootPoolEntry> children = ImmutableList.builder();

    public AlternativeEntry.Builder(LootPoolEntry.Builder<?> ... children) {
        for (LootPoolEntry.Builder<?> builder : children) {
            this.children.add((Object)builder.build());
        }
    }

    @Override
    protected AlternativeEntry.Builder getThisBuilder() {
        return this;
    }

    @Override
    public AlternativeEntry.Builder alternatively(LootPoolEntry.Builder<?> builder) {
        this.children.add((Object)builder.build());
        return this;
    }

    @Override
    public LootPoolEntry build() {
        return new AlternativeEntry((List<LootPoolEntry>)this.children.build(), this.getConditions());
    }

    @Override
    protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
