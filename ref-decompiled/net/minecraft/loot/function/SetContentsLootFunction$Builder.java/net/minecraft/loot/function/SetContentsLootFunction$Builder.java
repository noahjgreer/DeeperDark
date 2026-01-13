/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.loot.ContainerComponentModifier;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetContentsLootFunction;

public static class SetContentsLootFunction.Builder
extends ConditionalLootFunction.Builder<SetContentsLootFunction.Builder> {
    private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
    private final ContainerComponentModifier<?> componentModifier;

    public SetContentsLootFunction.Builder(ContainerComponentModifier<?> componentModifier) {
        this.componentModifier = componentModifier;
    }

    @Override
    protected SetContentsLootFunction.Builder getThisBuilder() {
        return this;
    }

    public SetContentsLootFunction.Builder withEntry(LootPoolEntry.Builder<?> entryBuilder) {
        this.entries.add((Object)entryBuilder.build());
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetContentsLootFunction(this.getConditions(), this.componentModifier, (List<LootPoolEntry>)this.entries.build());
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
