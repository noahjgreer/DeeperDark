/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrower;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

class SculkVeinBlock.SculkVeinGrowChecker
extends MultifaceGrower.LichenGrowChecker {
    private final MultifaceGrower.GrowType[] growTypes;

    public SculkVeinBlock.SculkVeinGrowChecker(SculkVeinBlock block, MultifaceGrower.GrowType ... growTypes) {
        super(block);
        this.growTypes = growTypes;
    }

    @Override
    public boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
        BlockPos blockPos;
        BlockState blockState = world.getBlockState(growPos.offset(direction));
        if (blockState.isOf(Blocks.SCULK) || blockState.isOf(Blocks.SCULK_CATALYST) || blockState.isOf(Blocks.MOVING_PISTON)) {
            return false;
        }
        if (pos.getManhattanDistance(growPos) == 2 && world.getBlockState(blockPos = pos.offset(direction.getOpposite())).isSideSolidFullSquare(world, blockPos, direction)) {
            return false;
        }
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty() && !fluidState.isOf(Fluids.WATER)) {
            return false;
        }
        if (state.isIn(BlockTags.FIRE)) {
            return false;
        }
        return state.isReplaceable() || super.canGrow(world, pos, growPos, direction, state);
    }

    @Override
    public MultifaceGrower.GrowType[] getGrowTypes() {
        return this.growTypes;
    }

    @Override
    public boolean canGrow(BlockState state) {
        return !state.isOf(Blocks.SCULK_VEIN);
    }
}
