/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;

static class PandaEntity.PlayGoal
extends Goal {
    private final PandaEntity panda;

    public PandaEntity.PlayGoal(PandaEntity panda) {
        this.panda = panda;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        int j;
        if (!this.panda.isBaby() && !this.panda.isPlayful() || !this.panda.isOnGround()) {
            return false;
        }
        if (!this.panda.isIdle()) {
            return false;
        }
        float f = this.panda.getYaw() * ((float)Math.PI / 180);
        float g = -MathHelper.sin(f);
        float h = MathHelper.cos(f);
        int i = (double)Math.abs(g) > 0.5 ? MathHelper.sign(g) : 0;
        int n = j = (double)Math.abs(h) > 0.5 ? MathHelper.sign(h) : 0;
        if (this.panda.getEntityWorld().getBlockState(this.panda.getBlockPos().add(i, -1, j)).isAir()) {
            return true;
        }
        if (this.panda.isPlayful() && this.panda.random.nextInt(PandaEntity.PlayGoal.toGoalTicks(60)) == 1) {
            return true;
        }
        return this.panda.random.nextInt(PandaEntity.PlayGoal.toGoalTicks(500)) == 1;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void start() {
        this.panda.setPlaying(true);
    }

    @Override
    public boolean canStop() {
        return false;
    }
}
