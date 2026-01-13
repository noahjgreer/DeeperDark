/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;

static class SnifferBrain.3
extends TemptTask {
    SnifferBrain.3(Function function, Function function2) {
        super(function, function2);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        SnifferBrain.stopDiggingOrSniffing((SnifferEntity)pathAwareEntity);
        super.run(serverWorld, pathAwareEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }
}
