/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class EnderChestBlockEntity.1
extends ViewerCountManager {
    EnderChestBlockEntity.1() {
    }

    @Override
    protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    protected void onContainerClose(World world, BlockPos pos, BlockState state) {
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        world.addSyncedBlockEvent(EnderChestBlockEntity.this.pos, Blocks.ENDER_CHEST, 1, newViewerCount);
    }

    @Override
    public boolean isPlayerViewing(PlayerEntity player) {
        return player.getEnderChestInventory().isActiveBlockEntity(EnderChestBlockEntity.this);
    }
}
