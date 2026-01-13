/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class SpawnerMinecartEntity.1
extends MobSpawnerLogic {
    SpawnerMinecartEntity.1() {
    }

    @Override
    public void sendStatus(World world, BlockPos pos, int status) {
        world.sendEntityStatus(SpawnerMinecartEntity.this, (byte)status);
    }
}
