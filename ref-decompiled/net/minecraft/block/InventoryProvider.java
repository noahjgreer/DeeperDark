/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.InventoryProvider
 *  net.minecraft.inventory.SidedInventory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface InventoryProvider {
    public SidedInventory getInventory(BlockState var1, WorldAccess var2, BlockPos var3);
}

