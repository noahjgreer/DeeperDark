/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

public static interface MultifaceGrower.GrowChecker {
    public @Nullable BlockState getStateWithDirection(BlockState var1, BlockView var2, BlockPos var3, Direction var4);

    public boolean canGrow(BlockView var1, BlockPos var2, MultifaceGrower.GrowPos var3);

    default public MultifaceGrower.GrowType[] getGrowTypes() {
        return GROW_TYPES;
    }

    default public boolean hasDirection(BlockState state, Direction direction) {
        return MultifaceBlock.hasDirection(state, direction);
    }

    default public boolean canGrow(BlockState state) {
        return false;
    }

    default public boolean canGrow(BlockState state, Direction direction) {
        return this.canGrow(state) || this.hasDirection(state, direction);
    }

    default public boolean place(WorldAccess world, MultifaceGrower.GrowPos growPos, BlockState state, boolean markForPostProcessing) {
        BlockState blockState = this.getStateWithDirection(state, world, growPos.pos(), growPos.face());
        if (blockState != null) {
            if (markForPostProcessing) {
                world.getChunk(growPos.pos()).markBlockForPostProcessing(growPos.pos());
            }
            return world.setBlockState(growPos.pos(), blockState, 2);
        }
        return false;
    }
}
