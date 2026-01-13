/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

class PhantomEntity.SwoopMovementGoal
extends PhantomEntity.MovementGoal {
    private static final int CAT_CHECK_INTERVAL = 20;
    private boolean catsNearby;
    private int nextCatCheckAge;

    PhantomEntity.SwoopMovementGoal() {
        super(PhantomEntity.this);
    }

    @Override
    public boolean canStart() {
        return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.SWOOP;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = PhantomEntity.this.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)livingEntity;
            if (livingEntity.isSpectator() || playerEntity.isCreative()) {
                return false;
            }
        }
        if (!this.canStart()) {
            return false;
        }
        if (PhantomEntity.this.age > this.nextCatCheckAge) {
            this.nextCatCheckAge = PhantomEntity.this.age + 20;
            List<Entity> list = PhantomEntity.this.getEntityWorld().getEntitiesByClass(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
            for (CatEntity catEntity : list) {
                catEntity.hiss();
            }
            this.catsNearby = !list.isEmpty();
        }
        return !this.catsNearby;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        PhantomEntity.this.setTarget(null);
        PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = PhantomEntity.this.getTarget();
        if (livingEntity == null) {
            return;
        }
        PhantomEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
        if (PhantomEntity.this.getBoundingBox().expand(0.2f).intersects(livingEntity.getBoundingBox())) {
            PhantomEntity.this.tryAttack(PhantomEntity.SwoopMovementGoal.castToServerWorld(PhantomEntity.this.getEntityWorld()), livingEntity);
            PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
            if (!PhantomEntity.this.isSilent()) {
                PhantomEntity.this.getEntityWorld().syncWorldEvent(1039, PhantomEntity.this.getBlockPos(), 0);
            }
        } else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
            PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
        }
    }
}
