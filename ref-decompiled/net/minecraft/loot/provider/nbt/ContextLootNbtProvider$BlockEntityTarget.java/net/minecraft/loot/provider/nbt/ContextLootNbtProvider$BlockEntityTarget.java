/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.provider.nbt;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.context.ContextParameter;

record ContextLootNbtProvider.BlockEntityTarget(ContextParameter<? extends BlockEntity> contextParam) implements LootEntityValueSource.ContextComponentBased<BlockEntity, NbtElement>
{
    @Override
    public NbtElement get(BlockEntity blockEntity) {
        return blockEntity.createNbtWithIdentifyingData(blockEntity.getWorld().getRegistryManager());
    }
}
