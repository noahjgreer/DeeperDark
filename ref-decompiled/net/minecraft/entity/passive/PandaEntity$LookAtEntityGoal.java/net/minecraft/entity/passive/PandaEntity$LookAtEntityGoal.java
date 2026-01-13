/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

static class PandaEntity.LookAtEntityGoal
extends LookAtEntityGoal {
    private final PandaEntity panda;

    public PandaEntity.LookAtEntityGoal(PandaEntity panda, Class<? extends LivingEntity> targetType, float range) {
        super(panda, targetType, range);
        this.panda = panda;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && super.shouldContinue();
    }

    @Override
    public boolean canStart() {
        if (this.mob.getRandom().nextFloat() >= this.chance) {
            return false;
        }
        if (this.target == null) {
            ServerWorld serverWorld = PandaEntity.LookAtEntityGoal.getServerWorld(this.mob);
            this.target = this.targetType == PlayerEntity.class ? serverWorld.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : serverWorld.getClosestEntity(this.mob.getEntityWorld().getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand(this.range, 3.0, this.range), livingEntity -> true), this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
        return this.panda.isIdle() && this.target != null;
    }

    @Override
    public void tick() {
        if (this.target != null) {
            super.tick();
        }
    }
}
