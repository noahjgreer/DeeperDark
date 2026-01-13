/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

class VexEntity.ChargeTargetGoal
extends Goal {
    public VexEntity.ChargeTargetGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = VexEntity.this.getTarget();
        if (livingEntity != null && livingEntity.isAlive() && !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(VexEntity.ChargeTargetGoal.toGoalTicks(7)) == 0) {
            return VexEntity.this.squaredDistanceTo(livingEntity) > 4.0;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return VexEntity.this.getMoveControl().isMoving() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
    }

    @Override
    public void start() {
        LivingEntity livingEntity = VexEntity.this.getTarget();
        if (livingEntity != null) {
            Vec3d vec3d = livingEntity.getEyePos();
            VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
        }
        VexEntity.this.setCharging(true);
        VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0f, 1.0f);
    }

    @Override
    public void stop() {
        VexEntity.this.setCharging(false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = VexEntity.this.getTarget();
        if (livingEntity == null) {
            return;
        }
        if (VexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
            VexEntity.this.tryAttack(VexEntity.ChargeTargetGoal.castToServerWorld(VexEntity.this.getEntityWorld()), livingEntity);
            VexEntity.this.setCharging(false);
        } else {
            double d = VexEntity.this.squaredDistanceTo(livingEntity);
            if (d < 9.0) {
                Vec3d vec3d = livingEntity.getEyePos();
                VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
        }
    }
}
