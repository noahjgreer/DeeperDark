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
import net.minecraft.world.World;

final class EnderDragonSpawnState.4
extends EnderDragonSpawnState {
    @Override
    public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
        if (tick >= 100) {
            fight.setSpawnState(END);
            fight.resetEndCrystals();
            for (EndCrystalEntity endCrystalEntity : crystals) {
                endCrystalEntity.setBeamTarget(null);
                world.createExplosion(endCrystalEntity, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), 6.0f, World.ExplosionSourceType.NONE);
                endCrystalEntity.discard();
            }
        } else if (tick >= 80) {
            world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
        } else if (tick == 0) {
            for (EndCrystalEntity endCrystalEntity : crystals) {
                endCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
            }
        } else if (tick < 5) {
            world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
        }
    }
}
