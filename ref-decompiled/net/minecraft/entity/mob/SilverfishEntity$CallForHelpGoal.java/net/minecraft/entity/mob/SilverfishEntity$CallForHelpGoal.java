/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;

static class SilverfishEntity.CallForHelpGoal
extends Goal {
    private final SilverfishEntity silverfish;
    private int delay;

    public SilverfishEntity.CallForHelpGoal(SilverfishEntity silverfish) {
        this.silverfish = silverfish;
    }

    public void onHurt() {
        if (this.delay == 0) {
            this.delay = this.getTickCount(20);
        }
    }

    @Override
    public boolean canStart() {
        return this.delay > 0;
    }

    @Override
    public void tick() {
        --this.delay;
        if (this.delay <= 0) {
            World world = this.silverfish.getEntityWorld();
            Random random = this.silverfish.getRandom();
            BlockPos blockPos = this.silverfish.getBlockPos();
            int i = 0;
            block0: while (i <= 5 && i >= -5) {
                int j = 0;
                while (j <= 10 && j >= -10) {
                    int k = 0;
                    while (k <= 10 && k >= -10) {
                        BlockPos blockPos2 = blockPos.add(j, i, k);
                        BlockState blockState = world.getBlockState(blockPos2);
                        Block block = blockState.getBlock();
                        if (block instanceof InfestedBlock) {
                            if (SilverfishEntity.CallForHelpGoal.castToServerWorld(world).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
                                world.breakBlock(blockPos2, true, this.silverfish);
                            } else {
                                world.setBlockState(blockPos2, ((InfestedBlock)block).toRegularState(world.getBlockState(blockPos2)), 3);
                            }
                            if (random.nextBoolean()) break block0;
                        }
                        k = (k <= 0 ? 1 : 0) - k;
                    }
                    j = (j <= 0 ? 1 : 0) - j;
                }
                i = (i <= 0 ? 1 : 0) - i;
            }
        }
    }
}
