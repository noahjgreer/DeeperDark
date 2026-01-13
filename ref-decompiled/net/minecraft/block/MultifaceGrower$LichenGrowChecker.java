/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jspecify.annotations.Nullable;

public static class MultifaceGrower.LichenGrowChecker
implements MultifaceGrower.GrowChecker {
    protected MultifaceBlock lichen;

    public MultifaceGrower.LichenGrowChecker(MultifaceBlock lichen) {
        this.lichen = lichen;
    }

    @Override
    public @Nullable BlockState getStateWithDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.lichen.withDirection(state, world, pos, direction);
    }

    protected boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
        return state.isAir() || state.isOf(this.lichen) || state.isOf(Blocks.WATER) && state.getFluidState().isStill();
    }

    @Override
    public boolean canGrow(BlockView world, BlockPos pos, MultifaceGrower.GrowPos growPos) {
        BlockState blockState = world.getBlockState(growPos.pos());
        return this.canGrow(world, pos, growPos.pos(), growPos.face(), blockState) && this.lichen.canGrowWithDirection(world, blockState, growPos.pos(), growPos.face());
    }
}
