/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

class SpawnLocationTypes.1
implements SpawnLocation {
    SpawnLocationTypes.1() {
    }

    @Override
    public boolean isSpawnPositionOk(WorldView worldView, BlockPos blockPos, @Nullable EntityType<?> entityType) {
        if (entityType == null || !worldView.getWorldBorder().contains(blockPos)) {
            return false;
        }
        BlockPos blockPos2 = blockPos.up();
        BlockPos blockPos3 = blockPos.down();
        BlockState blockState = worldView.getBlockState(blockPos3);
        if (!blockState.allowsSpawning(worldView, blockPos3, entityType)) {
            return false;
        }
        return this.isClearForSpawn(worldView, blockPos, entityType) && this.isClearForSpawn(worldView, blockPos2, entityType);
    }

    private boolean isClearForSpawn(WorldView world, BlockPos pos, EntityType<?> entityType) {
        BlockState blockState = world.getBlockState(pos);
        return SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), entityType);
    }

    @Override
    public BlockPos adjustPosition(WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        if (world.getBlockState(blockPos).canPathfindThrough(NavigationType.LAND)) {
            return blockPos;
        }
        return pos;
    }
}
