/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public static final class MoveItemsTask.Storage
extends Record {
    final BlockPos pos;
    final Inventory inventory;
    final BlockEntity blockEntity;
    final BlockState state;

    public MoveItemsTask.Storage(BlockPos pos, Inventory inventory, BlockEntity blockEntity, BlockState state) {
        this.pos = pos;
        this.inventory = inventory;
        this.blockEntity = blockEntity;
        this.state = state;
    }

    public static @Nullable MoveItemsTask.Storage forContainer(BlockEntity blockEntity, World world) {
        BlockPos blockPos = blockEntity.getPos();
        BlockState blockState = blockEntity.getCachedState();
        Inventory inventory = MoveItemsTask.Storage.getInventory(blockEntity, blockState, world, blockPos);
        if (inventory != null) {
            return new MoveItemsTask.Storage(blockPos, inventory, blockEntity, blockState);
        }
        return null;
    }

    public static @Nullable MoveItemsTask.Storage forContainer(BlockPos pos, World world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity == null ? null : MoveItemsTask.Storage.forContainer(blockEntity, world);
    }

    private static @Nullable Inventory getInventory(BlockEntity blockEntity, BlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof ChestBlock) {
            ChestBlock chestBlock = (ChestBlock)block;
            return ChestBlock.getInventory(chestBlock, state, world, pos, false);
        }
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory)((Object)blockEntity);
            return inventory;
        }
        return null;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MoveItemsTask.Storage.class, "pos;container;blockEntity;state", "pos", "inventory", "blockEntity", "state"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MoveItemsTask.Storage.class, "pos;container;blockEntity;state", "pos", "inventory", "blockEntity", "state"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MoveItemsTask.Storage.class, "pos;container;blockEntity;state", "pos", "inventory", "blockEntity", "state"}, this, object);
    }

    public BlockPos pos() {
        return this.pos;
    }

    public Inventory inventory() {
        return this.inventory;
    }

    public BlockEntity blockEntity() {
        return this.blockEntity;
    }

    public BlockState state() {
        return this.state;
    }
}
