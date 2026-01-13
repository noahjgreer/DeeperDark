/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.TrappedChestBlock
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.TrappedChestBlockEntity
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;

public class TrappedChestBlockEntity
extends ChestBlockEntity {
    public TrappedChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.TRAPPED_CHEST, blockPos, blockState);
    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        super.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
        if (oldViewerCount != newViewerCount) {
            WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)((Direction)state.get((Property)TrappedChestBlock.FACING)).getOpposite(), (Direction)Direction.UP);
            Block block = state.getBlock();
            world.updateNeighborsAlways(pos, block, wireOrientation);
            world.updateNeighborsAlways(pos.down(), block, wireOrientation);
        }
    }
}

