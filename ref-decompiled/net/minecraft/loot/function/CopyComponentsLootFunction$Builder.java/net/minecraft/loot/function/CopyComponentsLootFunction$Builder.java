/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.LootFunction;

public static class CopyComponentsLootFunction.Builder
extends ConditionalLootFunction.Builder<CopyComponentsLootFunction.Builder> {
    private final LootEntityValueSource<ComponentsAccess> source;
    private Optional<ImmutableList.Builder<ComponentType<?>>> include = Optional.empty();
    private Optional<ImmutableList.Builder<ComponentType<?>>> exclude = Optional.empty();

    CopyComponentsLootFunction.Builder(LootEntityValueSource<ComponentsAccess> source) {
        this.source = source;
    }

    public CopyComponentsLootFunction.Builder include(ComponentType<?> type) {
        if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
        }
        this.include.get().add(type);
        return this;
    }

    public CopyComponentsLootFunction.Builder exclude(ComponentType<?> type) {
        if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
        }
        this.exclude.get().add(type);
        return this;
    }

    @Override
    protected CopyComponentsLootFunction.Builder getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return new CopyComponentsLootFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
