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
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.LootPoolEntry;

public static class GroupEntry.Builder
extends LootPoolEntry.Builder<GroupEntry.Builder> {
    private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();

    public GroupEntry.Builder(LootPoolEntry.Builder<?> ... entries) {
        for (LootPoolEntry.Builder<?> builder : entries) {
            this.entries.add((Object)builder.build());
        }
    }

    @Override
    protected GroupEntry.Builder getThisBuilder() {
        return this;
    }

    @Override
    public GroupEntry.Builder groupEntry(LootPoolEntry.Builder<?> entry) {
        this.entries.add((Object)entry.build());
        return this;
    }

    @Override
    public LootPoolEntry build() {
        return new GroupEntry((List<LootPoolEntry>)this.entries.build(), this.getConditions());
    }

    @Override
    protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
