/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;

static class SnifferBrain.1
extends FleeTask<SnifferEntity> {
    SnifferBrain.1(float f) {
        super(f);
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        SnifferBrain.stopDiggingOrSniffing(snifferEntity);
        super.run(serverWorld, snifferEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        this.run(serverWorld, (SnifferEntity)pathAwareEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (SnifferEntity)entity, time);
    }
}
