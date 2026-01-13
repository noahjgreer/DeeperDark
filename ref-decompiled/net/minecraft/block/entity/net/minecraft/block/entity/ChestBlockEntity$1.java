/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class ChestBlockEntity.1
extends ViewerCountManager {
    ChestBlockEntity.1() {
    }

    @Override
    protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            ChestBlockEntity.playSound(world, pos, state, chestBlock.getOpenSound());
        }
    }

    @Override
    protected void onContainerClose(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            ChestBlockEntity.playSound(world, pos, state, chestBlock.getCloseSound());
        }
    }

    @Override
    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        ChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
    }

    @Override
    public boolean isPlayerViewing(PlayerEntity player) {
        if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
            return inventory == ChestBlockEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(ChestBlockEntity.this);
        }
        return false;
    }
}
