/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;

static class GuardianEntity.FireBeamGoal
extends Goal {
    private final GuardianEntity guardian;
    private int beamTicks;
    private final boolean elder;

    public GuardianEntity.FireBeamGoal(GuardianEntity guardian) {
        this.guardian = guardian;
        this.elder = guardian instanceof ElderGuardianEntity;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.guardian.getTarget();
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && (this.elder || this.guardian.getTarget() != null && this.guardian.squaredDistanceTo(this.guardian.getTarget()) > 9.0);
    }

    @Override
    public void start() {
        this.beamTicks = -10;
        this.guardian.getNavigation().stop();
        LivingEntity livingEntity = this.guardian.getTarget();
        if (livingEntity != null) {
            this.guardian.getLookControl().lookAt(livingEntity, 90.0f, 90.0f);
        }
        this.guardian.velocityDirty = true;
    }

    @Override
    public void stop() {
        this.guardian.setBeamTarget(0);
        this.guardian.setTarget(null);
        this.guardian.wanderGoal.ignoreChanceOnce();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.guardian.getTarget();
        if (livingEntity == null) {
            return;
        }
        this.guardian.getNavigation().stop();
        this.guardian.getLookControl().lookAt(livingEntity, 90.0f, 90.0f);
        if (!this.guardian.canSee(livingEntity)) {
            this.guardian.setTarget(null);
            return;
        }
        ++this.beamTicks;
        if (this.beamTicks == 0) {
            this.guardian.setBeamTarget(livingEntity.getId());
            if (!this.guardian.isSilent()) {
                this.guardian.getEntityWorld().sendEntityStatus(this.guardian, (byte)21);
            }
        } else if (this.beamTicks >= this.guardian.getWarmupTime()) {
            float f = 1.0f;
            if (this.guardian.getEntityWorld().getDifficulty() == Difficulty.HARD) {
                f += 2.0f;
            }
            if (this.elder) {
                f += 2.0f;
            }
            ServerWorld serverWorld = GuardianEntity.FireBeamGoal.getServerWorld(this.guardian);
            livingEntity.damage(serverWorld, this.guardian.getDamageSources().indirectMagic(this.guardian, this.guardian), f);
            this.guardian.tryAttack(serverWorld, livingEntity);
            this.guardian.setTarget(null);
        }
        super.tick();
    }
}
