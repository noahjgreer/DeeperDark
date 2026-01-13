/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.MultifaceGrowthBlock
 *  net.minecraft.block.SculkSpreadable
 *  net.minecraft.block.entity.SculkSpreadManager
 *  net.minecraft.block.entity.SculkSpreadManager$Cursor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.WorldAccess
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.Collection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

public interface SculkSpreadable {
    public static final SculkSpreadable VEIN_ONLY_SPREADER = new /* Unavailable Anonymous Inner Class!! */;

    default public byte getUpdate() {
        return 1;
    }

    default public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
    }

    default public boolean method_41470(WorldAccess world, BlockPos pos, Random random) {
        return false;
    }

    default public boolean spread(WorldAccess world, BlockPos pos, BlockState state, @Nullable Collection<Direction> directions, boolean markForPostProcessing) {
        return ((MultifaceGrowthBlock)Blocks.SCULK_VEIN).getGrower().grow(state, world, pos, markForPostProcessing) > 0L;
    }

    default public boolean shouldConvertToSpreadable() {
        return true;
    }

    default public int getDecay(int oldDecay) {
        return 1;
    }

    public int spread(SculkSpreadManager.Cursor var1, WorldAccess var2, BlockPos var3, Random var4, SculkSpreadManager var5, boolean var6);
}

