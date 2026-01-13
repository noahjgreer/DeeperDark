/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;

public static class LootTable.Builder
implements LootFunctionConsumingBuilder<LootTable.Builder>,
FabricLootTableBuilder {
    private final ImmutableList.Builder<LootPool> pools = ImmutableList.builder();
    private final ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();
    private ContextType type = GENERIC;
    private Optional<Identifier> randomSequenceId = Optional.empty();

    public LootTable.Builder pool(LootPool.Builder poolBuilder) {
        this.pools.add((Object)poolBuilder.build());
        return this;
    }

    public LootTable.Builder type(ContextType type) {
        this.type = type;
        return this;
    }

    public LootTable.Builder randomSequenceId(Identifier randomSequenceId) {
        this.randomSequenceId = Optional.of(randomSequenceId);
        return this;
    }

    @Override
    public LootTable.Builder apply(LootFunction.Builder builder) {
        this.functions.add((Object)builder.build());
        return this;
    }

    @Override
    public LootTable.Builder getThisFunctionConsumingBuilder() {
        return this;
    }

    public LootTable build() {
        return new LootTable(this.type, this.randomSequenceId, (List<LootPool>)this.pools.build(), (List<LootFunction>)this.functions.build());
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
        return this.getThisFunctionConsumingBuilder();
    }

    @Override
    public /* synthetic */ LootFunctionConsumingBuilder apply(LootFunction.Builder function) {
        return this.apply(function);
    }
}
