/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

static class PandaEntity.PandaMateGoal
extends AnimalMateGoal {
    private final PandaEntity panda;
    private int nextAskPlayerForBambooAge;

    public PandaEntity.PandaMateGoal(PandaEntity panda, double chance) {
        super(panda, chance);
        this.panda = panda;
    }

    @Override
    public boolean canStart() {
        if (super.canStart() && this.panda.getAskForBambooTicks() == 0) {
            if (!this.isBambooClose()) {
                if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                    this.panda.setAskForBambooTicks(32);
                    this.nextAskPlayerForBambooAge = this.panda.age + 600;
                    if (this.panda.canActVoluntarily()) {
                        PlayerEntity playerEntity = this.world.getClosestPlayer(ASK_FOR_BAMBOO_TARGET, this.panda);
                        this.panda.lookAtPlayerGoal.setTarget(playerEntity);
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isBambooClose() {
        BlockPos blockPos = this.panda.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 8; ++j) {
                int k = 0;
                while (k <= j) {
                    int l;
                    int n = l = k < j && k > -j ? j : 0;
                    while (l <= j) {
                        mutable.set(blockPos, k, i, l);
                        if (this.world.getBlockState(mutable).isOf(Blocks.BAMBOO)) {
                            return true;
                        }
                        l = l > 0 ? -l : 1 - l;
                    }
                    k = k > 0 ? -k : 1 - k;
                }
            }
        }
        return false;
    }
}
