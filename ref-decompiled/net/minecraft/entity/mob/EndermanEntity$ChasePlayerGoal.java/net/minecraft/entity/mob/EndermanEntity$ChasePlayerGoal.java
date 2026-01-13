/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jspecify.annotations.Nullable;

static class EndermanEntity.ChasePlayerGoal
extends Goal {
    private final EndermanEntity enderman;
    private @Nullable LivingEntity target;

    public EndermanEntity.ChasePlayerGoal(EndermanEntity enderman) {
        this.enderman = enderman;
        this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        this.target = this.enderman.getTarget();
        LivingEntity livingEntity = this.target;
        if (!(livingEntity instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity playerEntity = (PlayerEntity)livingEntity;
        double d = this.target.squaredDistanceTo(this.enderman);
        if (d > 256.0) {
            return false;
        }
        return this.enderman.isPlayerStaring(playerEntity);
    }

    @Override
    public void start() {
        this.enderman.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.enderman.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
    }
}
