/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

class PhantomEntity.StartAttackGoal
extends Goal {
    private int cooldown;

    PhantomEntity.StartAttackGoal() {
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = PhantomEntity.this.getTarget();
        if (livingEntity != null) {
            return PhantomEntity.this.testTargetPredicate(PhantomEntity.StartAttackGoal.castToServerWorld(PhantomEntity.this.getEntityWorld()), livingEntity, TargetPredicate.DEFAULT);
        }
        return false;
    }

    @Override
    public void start() {
        this.cooldown = this.getTickCount(10);
        PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
        this.startSwoop();
    }

    @Override
    public void stop() {
        if (PhantomEntity.this.circlingCenter != null) {
            PhantomEntity.this.circlingCenter = PhantomEntity.this.getEntityWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.circlingCenter).up(10 + PhantomEntity.this.random.nextInt(20));
        }
    }

    @Override
    public void tick() {
        if (PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.CIRCLE) {
            --this.cooldown;
            if (this.cooldown <= 0) {
                PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.SWOOP;
                this.startSwoop();
                this.cooldown = this.getTickCount((8 + PhantomEntity.this.random.nextInt(4)) * 20);
                PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0f, 0.95f + PhantomEntity.this.random.nextFloat() * 0.1f);
            }
        }
    }

    private void startSwoop() {
        if (PhantomEntity.this.circlingCenter == null) {
            return;
        }
        PhantomEntity.this.circlingCenter = PhantomEntity.this.getTarget().getBlockPos().up(20 + PhantomEntity.this.random.nextInt(20));
        if (PhantomEntity.this.circlingCenter.getY() < PhantomEntity.this.getEntityWorld().getSeaLevel()) {
            PhantomEntity.this.circlingCenter = new BlockPos(PhantomEntity.this.circlingCenter.getX(), PhantomEntity.this.getEntityWorld().getSeaLevel() + 1, PhantomEntity.this.circlingCenter.getZ());
        }
    }
}
