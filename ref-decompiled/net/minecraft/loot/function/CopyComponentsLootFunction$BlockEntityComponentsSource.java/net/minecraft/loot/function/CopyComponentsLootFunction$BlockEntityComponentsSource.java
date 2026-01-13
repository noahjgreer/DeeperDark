/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.context.ContextParameter;

record CopyComponentsLootFunction.BlockEntityComponentsSource(ContextParameter<? extends BlockEntity> contextParam) implements LootEntityValueSource.ContextComponentBased<BlockEntity, ComponentsAccess>
{
    @Override
    public ComponentsAccess get(BlockEntity blockEntity) {
        return blockEntity.createComponentMap();
    }
}
