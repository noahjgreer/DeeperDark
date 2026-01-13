/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

final class SideShapeType.3
extends SideShapeType {
    private final VoxelShape hollowSquareCuboid = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createColumnShape(12.0, 0.0, 16.0), BooleanBiFunction.ONLY_FIRST);

    @Override
    public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return !VoxelShapes.matchesAnywhere(state.getSidesShape(world, pos).getFace(direction), this.hollowSquareCuboid, BooleanBiFunction.ONLY_SECOND);
    }
}
