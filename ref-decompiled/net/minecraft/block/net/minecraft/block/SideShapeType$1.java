/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

final class SideShapeType.1
extends SideShapeType {
    @Override
    public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return Block.isFaceFullSquare(state.getSidesShape(world, pos), direction);
    }
}
