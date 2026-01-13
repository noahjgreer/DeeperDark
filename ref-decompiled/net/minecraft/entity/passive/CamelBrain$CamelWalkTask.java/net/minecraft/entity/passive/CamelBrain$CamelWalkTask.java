/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.server.world.ServerWorld;

public static class CamelBrain.CamelWalkTask
extends FleeTask<CamelEntity> {
    public CamelBrain.CamelWalkTask(float f) {
        super(f);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, CamelEntity camelEntity) {
        return super.shouldRun(serverWorld, camelEntity) && !camelEntity.isControlledByMob();
    }

    @Override
    protected void run(ServerWorld serverWorld, CamelEntity camelEntity, long l) {
        camelEntity.setStanding();
        super.run(serverWorld, camelEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        this.run(serverWorld, (CamelEntity)pathAwareEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (CamelEntity)entity, time);
    }
}
