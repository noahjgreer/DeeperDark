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
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.SequenceEntry;

public static class SequenceEntry.Builder
extends LootPoolEntry.Builder<SequenceEntry.Builder> {
    private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();

    public SequenceEntry.Builder(LootPoolEntry.Builder<?> ... entries) {
        for (LootPoolEntry.Builder<?> builder : entries) {
            this.entries.add((Object)builder.build());
        }
    }

    @Override
    protected SequenceEntry.Builder getThisBuilder() {
        return this;
    }

    @Override
    public SequenceEntry.Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
        this.entries.add((Object)entry.build());
        return this;
    }

    @Override
    public LootPoolEntry build() {
        return new SequenceEntry((List<LootPoolEntry>)this.entries.build(), this.getConditions());
    }

    @Override
    protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
