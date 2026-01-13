/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.server.world.ServerWorld;

static class CreakingBrain.1
extends StayAboveWaterTask<CreakingEntity> {
    CreakingBrain.1(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, CreakingEntity creakingEntity) {
        return creakingEntity.isUnrooted() && super.shouldRun(serverWorld, (LivingEntity)creakingEntity);
    }
}
