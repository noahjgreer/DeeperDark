/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;

static class SnifferBrain.FeelHappyTask
extends MultiTickTask<SnifferEntity> {
    SnifferBrain.FeelHappyTask(int minRunTime, int maxRunTime) {
        super(Map.of(MemoryModuleType.SNIFFER_HAPPY, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.FEELING_HAPPY);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.IDLING);
        snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_HAPPY);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (SnifferEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (SnifferEntity)entity, time);
    }
}
