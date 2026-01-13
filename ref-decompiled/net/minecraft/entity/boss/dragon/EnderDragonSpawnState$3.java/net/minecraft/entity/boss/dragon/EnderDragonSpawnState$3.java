/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

final class EnderDragonSpawnState.3
extends EnderDragonSpawnState {
    @Override
    public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
        boolean bl2;
        int i = 40;
        boolean bl = tick % 40 == 0;
        boolean bl3 = bl2 = tick % 40 == 39;
        if (bl || bl2) {
            int j = tick / 40;
            List<EndSpikeFeature.Spike> list = EndSpikeFeature.getSpikes(world);
            if (j < list.size()) {
                EndSpikeFeature.Spike spike = list.get(j);
                if (bl) {
                    for (EndCrystalEntity endCrystalEntity : crystals) {
                        endCrystalEntity.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
                    }
                } else {
                    int k = 10;
                    for (BlockPos blockPos : BlockPos.iterate(new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10), new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10))) {
                        world.removeBlock(blockPos, false);
                    }
                    world.createExplosion(null, (float)spike.getCenterX() + 0.5f, spike.getHeight(), (float)spike.getCenterZ() + 0.5f, 5.0f, World.ExplosionSourceType.BLOCK);
                    EndSpikeFeatureConfig endSpikeFeatureConfig = new EndSpikeFeatureConfig(true, (List<EndSpikeFeature.Spike>)ImmutableList.of((Object)spike), new BlockPos(0, 128, 0));
                    Feature.END_SPIKE.generateIfValid(endSpikeFeatureConfig, world, world.getChunkManager().getChunkGenerator(), Random.create(), new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()));
                }
            } else if (bl) {
                fight.setSpawnState(SUMMONING_DRAGON);
            }
        }
    }
}
