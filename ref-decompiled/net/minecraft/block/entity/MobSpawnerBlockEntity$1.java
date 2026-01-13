/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

class MobSpawnerBlockEntity.1
extends MobSpawnerLogic {
    MobSpawnerBlockEntity.1(MobSpawnerBlockEntity mobSpawnerBlockEntity) {
    }

    @Override
    public void sendStatus(World world, BlockPos pos, int status) {
        world.addSyncedBlockEvent(pos, Blocks.SPAWNER, status, 0);
    }

    @Override
    public void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
        super.setSpawnEntry(world, pos, spawnEntry);
        if (world != null) {
            BlockState blockState = world.getBlockState(pos);
            world.updateListeners(pos, blockState, blockState, 260);
        }
    }
}
