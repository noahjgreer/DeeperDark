/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.boss.dragon;

import java.util.List;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

final class EnderDragonSpawnState.2
extends EnderDragonSpawnState {
    @Override
    public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
        if (tick < 100) {
            if (tick == 0 || tick == 50 || tick == 51 || tick == 52 || tick >= 95) {
                world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            }
        } else {
            fight.setSpawnState(SUMMONING_PILLARS);
        }
    }
}
