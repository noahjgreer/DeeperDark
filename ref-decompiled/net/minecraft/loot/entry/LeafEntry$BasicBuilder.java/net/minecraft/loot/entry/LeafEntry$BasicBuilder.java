/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;

static class LeafEntry.BasicBuilder
extends LeafEntry.Builder<LeafEntry.BasicBuilder> {
    private final LeafEntry.Factory factory;

    public LeafEntry.BasicBuilder(LeafEntry.Factory factory) {
        this.factory = factory;
    }

    @Override
    protected LeafEntry.BasicBuilder getThisBuilder() {
        return this;
    }

    @Override
    public LootPoolEntry build() {
        return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
    }

    @Override
    protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
