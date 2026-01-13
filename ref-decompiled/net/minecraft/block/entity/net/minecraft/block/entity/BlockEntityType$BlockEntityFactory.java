/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
static interface BlockEntityType.BlockEntityFactory<T extends BlockEntity> {
    public T create(BlockPos var1, BlockState var2);
}
