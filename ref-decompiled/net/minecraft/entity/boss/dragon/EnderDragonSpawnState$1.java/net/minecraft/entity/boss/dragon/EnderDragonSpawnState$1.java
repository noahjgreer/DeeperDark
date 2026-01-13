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

final class EnderDragonSpawnState.1
extends EnderDragonSpawnState {
    @Override
    public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
        BlockPos blockPos = new BlockPos(0, 128, 0);
        for (EndCrystalEntity endCrystalEntity : crystals) {
            endCrystalEntity.setBeamTarget(blockPos);
        }
        fight.setSpawnState(PREPARING_TO_SUMMON_PILLARS);
    }
}
