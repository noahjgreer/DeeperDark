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
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;

public static abstract class LeafEntry.Builder<T extends LeafEntry.Builder<T>>
extends LootPoolEntry.Builder<T>
implements LootFunctionConsumingBuilder<T> {
    protected int weight = 1;
    protected int quality = 0;
    private final ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();

    @Override
    public T apply(LootFunction.Builder builder) {
        this.functions.add((Object)builder.build());
        return (T)((LeafEntry.Builder)this.getThisBuilder());
    }

    protected List<LootFunction> getFunctions() {
        return this.functions.build();
    }

    public T weight(int weight) {
        this.weight = weight;
        return (T)((LeafEntry.Builder)this.getThisBuilder());
    }

    public T quality(int quality) {
        this.quality = quality;
        return (T)((LeafEntry.Builder)this.getThisBuilder());
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
        return (LootFunctionConsumingBuilder)((Object)super.getThisConditionConsumingBuilder());
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder apply(LootFunction.Builder function) {
        return this.apply(function);
    }
}
