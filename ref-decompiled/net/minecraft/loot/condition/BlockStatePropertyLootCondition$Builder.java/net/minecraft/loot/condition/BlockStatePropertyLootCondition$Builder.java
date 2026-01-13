/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.condition;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.entry.RegistryEntry;

public static class BlockStatePropertyLootCondition.Builder
implements LootCondition.Builder {
    private final RegistryEntry<Block> block;
    private Optional<StatePredicate> propertyValues = Optional.empty();

    public BlockStatePropertyLootCondition.Builder(Block block) {
        this.block = block.getRegistryEntry();
    }

    public BlockStatePropertyLootCondition.Builder properties(StatePredicate.Builder builder) {
        this.propertyValues = builder.build();
        return this;
    }

    @Override
    public LootCondition build() {
        return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
    }
}
