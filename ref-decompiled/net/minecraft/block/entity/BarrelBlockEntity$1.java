/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class BarrelBlockEntity.1
extends ViewerCountManager {
    BarrelBlockEntity.1() {
    }

    @Override
    protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
        BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
        BarrelBlockEntity.this.setOpen(state, true);
    }

    @Override
    protected void onContainerClose(World world, BlockPos pos, BlockState state) {
        BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
        BarrelBlockEntity.this.setOpen(state, false);
    }

    @Override
    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
    }

    @Override
    public boolean isPlayerViewing(PlayerEntity player) {
        if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
            return inventory == BarrelBlockEntity.this;
        }
        return false;
    }
}
