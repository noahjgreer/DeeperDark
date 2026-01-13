/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

static class SilverfishEntity.WanderAndInfestGoal
extends WanderAroundGoal {
    private @Nullable Direction direction;
    private boolean canInfest;

    public SilverfishEntity.WanderAndInfestGoal(SilverfishEntity silverfish) {
        super(silverfish, 1.0, 10);
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getTarget() != null) {
            return false;
        }
        if (!this.mob.getNavigation().isIdle()) {
            return false;
        }
        Random random = this.mob.getRandom();
        if (SilverfishEntity.WanderAndInfestGoal.getServerWorld(this.mob).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue() && random.nextInt(SilverfishEntity.WanderAndInfestGoal.toGoalTicks(10)) == 0) {
            this.direction = Direction.random(random);
            BlockPos blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).offset(this.direction);
            BlockState blockState = this.mob.getEntityWorld().getBlockState(blockPos);
            if (InfestedBlock.isInfestable(blockState)) {
                this.canInfest = true;
                return true;
            }
        }
        this.canInfest = false;
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.canInfest) {
            return false;
        }
        return super.shouldContinue();
    }

    @Override
    public void start() {
        BlockPos blockPos;
        if (!this.canInfest) {
            super.start();
            return;
        }
        World worldAccess = this.mob.getEntityWorld();
        BlockState blockState = worldAccess.getBlockState(blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).offset(this.direction));
        if (InfestedBlock.isInfestable(blockState)) {
            worldAccess.setBlockState(blockPos, InfestedBlock.fromRegularState(blockState), 3);
            this.mob.playSpawnEffects();
            this.mob.discard();
        }
    }
}
