/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PufferfishEntity;

static class PufferfishEntity.InflateGoal
extends Goal {
    private final PufferfishEntity pufferfish;

    public PufferfishEntity.InflateGoal(PufferfishEntity pufferfish) {
        this.pufferfish = pufferfish;
    }

    @Override
    public boolean canStart() {
        List<LivingEntity> list = this.pufferfish.getEntityWorld().getEntitiesByClass(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), livingEntity -> BLOW_UP_TARGET_PREDICATE.test(PufferfishEntity.InflateGoal.getServerWorld(this.pufferfish), this.pufferfish, (LivingEntity)livingEntity));
        return !list.isEmpty();
    }

    @Override
    public void start() {
        this.pufferfish.inflateTicks = 1;
        this.pufferfish.deflateTicks = 0;
    }

    @Override
    public void stop() {
        this.pufferfish.inflateTicks = 0;
    }
}
