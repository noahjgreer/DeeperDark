/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jspecify.annotations.Nullable;

static class DolphinEntity.SwimWithPlayerGoal
extends Goal {
    private final DolphinEntity dolphin;
    private final double speed;
    private @Nullable PlayerEntity closestPlayer;

    DolphinEntity.SwimWithPlayerGoal(DolphinEntity dolphin, double speed) {
        this.dolphin = dolphin;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.closestPlayer = DolphinEntity.SwimWithPlayerGoal.getServerWorld(this.dolphin).getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.dolphin);
        if (this.closestPlayer == null) {
            return false;
        }
        return this.closestPlayer.isSwimming() && this.dolphin.getTarget() != this.closestPlayer;
    }

    @Override
    public boolean shouldContinue() {
        return this.closestPlayer != null && this.closestPlayer.isSwimming() && this.dolphin.squaredDistanceTo(this.closestPlayer) < 256.0;
    }

    @Override
    public void start() {
        this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
    }

    @Override
    public void stop() {
        this.closestPlayer = null;
        this.dolphin.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.dolphin.getLookControl().lookAt(this.closestPlayer, this.dolphin.getMaxHeadRotation() + 20, this.dolphin.getMaxLookPitchChange());
        if (this.dolphin.squaredDistanceTo(this.closestPlayer) < 6.25) {
            this.dolphin.getNavigation().stop();
        } else {
            this.dolphin.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
        if (this.closestPlayer.isSwimming() && this.closestPlayer.getEntityWorld().random.nextInt(6) == 0) {
            this.closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), this.dolphin);
        }
    }
}
