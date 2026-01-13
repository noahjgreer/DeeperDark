/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

static class StriderEntity.GoBackToLavaGoal
extends MoveToTargetPosGoal {
    private final StriderEntity strider;

    StriderEntity.GoBackToLavaGoal(StriderEntity strider, double speed) {
        super(strider, speed, 8, 2);
        this.strider = strider;
    }

    @Override
    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    @Override
    public boolean shouldContinue() {
        return !this.strider.isInLava() && this.isTargetPos(this.strider.getEntityWorld(), this.targetPos);
    }

    @Override
    public boolean canStart() {
        return !this.strider.isInLava() && super.canStart();
    }

    @Override
    public boolean shouldResetPath() {
        return this.tryingTime % 20 == 0;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.LAVA) && world.getBlockState(pos.up()).canPathfindThrough(NavigationType.LAND);
    }
}
