/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;

public static class CopyStateLootFunction.Builder
extends ConditionalLootFunction.Builder<CopyStateLootFunction.Builder> {
    private final RegistryEntry<Block> block;
    private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

    CopyStateLootFunction.Builder(Block block) {
        this.block = block.getRegistryEntry();
    }

    public CopyStateLootFunction.Builder addProperty(Property<?> property) {
        if (!this.block.value().getStateManager().getProperties().contains(property)) {
            throw new IllegalStateException("Property " + String.valueOf(property) + " is not present on block " + String.valueOf(this.block));
        }
        this.properties.add(property);
        return this;
    }

    @Override
    protected CopyStateLootFunction.Builder getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return new CopyStateLootFunction(this.getConditions(), this.block, (Set<Property<?>>)this.properties.build());
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
