/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Collection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

class SculkSpreadable.1
implements SculkSpreadable {
    SculkSpreadable.1() {
    }

    @Override
    public boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
        if (directions == null) {
            return ((SculkVeinBlock)Blocks.SCULK_VEIN).getSamePositionOnlyGrower().grow(world.getBlockState(pos), world, pos, markForPostProcessing) > 0L;
        }
        if (!directions.isEmpty()) {
            if (state.isAir() || state.getFluidState().isOf(Fluids.WATER)) {
                return SculkVeinBlock.place(world, pos, state, directions);
            }
            return false;
        }
        return SculkSpreadable.super.spread(world, pos, state, directions, markForPostProcessing);
    }

    @Override
    public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        return cursor.getDecay() > 0 ? cursor.getCharge() : 0;
    }

    @Override
    public int getDecay(int oldDecay) {
        return Math.max(oldDecay - 1, 0);
    }
}
