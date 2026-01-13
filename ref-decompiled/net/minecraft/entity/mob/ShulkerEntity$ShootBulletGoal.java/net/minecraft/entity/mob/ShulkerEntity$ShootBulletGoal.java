/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;

class ShulkerEntity.ShootBulletGoal
extends Goal {
    private int counter;

    public ShulkerEntity.ShootBulletGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = ShulkerEntity.this.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        return ShulkerEntity.this.getEntityWorld().getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public void start() {
        this.counter = 20;
        ShulkerEntity.this.setPeekAmount(100);
    }

    @Override
    public void stop() {
        ShulkerEntity.this.setPeekAmount(0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (ShulkerEntity.this.getEntityWorld().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        --this.counter;
        LivingEntity livingEntity = ShulkerEntity.this.getTarget();
        if (livingEntity == null) {
            return;
        }
        ShulkerEntity.this.getLookControl().lookAt(livingEntity, 180.0f, 180.0f);
        double d = ShulkerEntity.this.squaredDistanceTo(livingEntity);
        if (d < 400.0) {
            if (this.counter <= 0) {
                this.counter = 20 + ShulkerEntity.this.random.nextInt(10) * 20 / 2;
                ShulkerEntity.this.getEntityWorld().spawnEntity(new ShulkerBulletEntity(ShulkerEntity.this.getEntityWorld(), ShulkerEntity.this, livingEntity, ShulkerEntity.this.getAttachedFace().getAxis()));
                ShulkerEntity.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0f, (ShulkerEntity.this.random.nextFloat() - ShulkerEntity.this.random.nextFloat()) * 0.2f + 1.0f);
            }
        } else {
            ShulkerEntity.this.setTarget(null);
        }
        super.tick();
    }
}
