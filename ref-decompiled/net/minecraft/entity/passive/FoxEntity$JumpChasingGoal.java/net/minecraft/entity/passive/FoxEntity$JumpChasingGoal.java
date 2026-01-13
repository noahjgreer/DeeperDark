/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FoxEntity.JumpChasingGoal
extends DiveJumpingGoal {
    @Override
    public boolean canStart() {
        if (!FoxEntity.this.isFullyCrouched()) {
            return false;
        }
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        if (livingEntity.getMovementDirection() != livingEntity.getHorizontalFacing()) {
            return false;
        }
        boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
        if (!bl) {
            FoxEntity.this.getNavigation().findPathTo(livingEntity, 0);
            FoxEntity.this.setCrouching(false);
            FoxEntity.this.setRollingHead(false);
        }
        return bl;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        double d = FoxEntity.this.getVelocity().y;
        return !(d * d < (double)0.05f && Math.abs(FoxEntity.this.getPitch()) < 15.0f && FoxEntity.this.isOnGround() || FoxEntity.this.isWalking());
    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void start() {
        FoxEntity.this.setJumping(true);
        FoxEntity.this.setChasing(true);
        FoxEntity.this.setRollingHead(false);
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity != null) {
            FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0f, 30.0f);
            Vec3d vec3d = new Vec3d(livingEntity.getX() - FoxEntity.this.getX(), livingEntity.getY() - FoxEntity.this.getY(), livingEntity.getZ() - FoxEntity.this.getZ()).normalize();
            FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
        }
        FoxEntity.this.getNavigation().stop();
    }

    @Override
    public void stop() {
        FoxEntity.this.setCrouching(false);
        FoxEntity.this.extraRollingHeight = 0.0f;
        FoxEntity.this.lastExtraRollingHeight = 0.0f;
        FoxEntity.this.setRollingHead(false);
        FoxEntity.this.setChasing(false);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = FoxEntity.this.getTarget();
        if (livingEntity != null) {
            FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0f, 30.0f);
        }
        if (!FoxEntity.this.isWalking()) {
            Vec3d vec3d = FoxEntity.this.getVelocity();
            if (vec3d.y * vec3d.y < (double)0.03f && FoxEntity.this.getPitch() != 0.0f) {
                FoxEntity.this.setPitch(MathHelper.lerpAngleDegrees(0.2f, FoxEntity.this.getPitch(), 0.0f));
            } else {
                double d = vec3d.horizontalLength();
                double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 57.2957763671875;
                FoxEntity.this.setPitch((float)e);
            }
        }
        if (livingEntity != null && FoxEntity.this.distanceTo(livingEntity) <= 2.0f) {
            FoxEntity.this.tryAttack(FoxEntity.JumpChasingGoal.castToServerWorld(FoxEntity.this.getEntityWorld()), livingEntity);
        } else if (FoxEntity.this.getPitch() > 0.0f && FoxEntity.this.isOnGround() && (float)FoxEntity.this.getVelocity().y != 0.0f && FoxEntity.this.getEntityWorld().getBlockState(FoxEntity.this.getBlockPos()).isOf(Blocks.SNOW)) {
            FoxEntity.this.setPitch(60.0f);
            FoxEntity.this.setTarget(null);
            FoxEntity.this.setWalking(true);
        }
    }
}
