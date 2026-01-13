/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class MultifaceGrower.GrowType.3
extends MultifaceGrower.GrowType {
    @Override
    public MultifaceGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
        return new MultifaceGrower.GrowPos(pos.offset(newDirection).offset(oldDirection), newDirection.getOpposite());
    }
}
