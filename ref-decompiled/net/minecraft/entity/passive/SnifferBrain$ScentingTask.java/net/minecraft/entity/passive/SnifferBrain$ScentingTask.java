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

static class SnifferBrain.ScentingTask
extends MultiTickTask<SnifferEntity> {
    SnifferBrain.ScentingTask(int minRunTime, int maxRunTime) {
        super(Map.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_DIGGING, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_HAPPY, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
        return !snifferEntity.isTempted();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        return true;
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.SCENTING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.IDLING);
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
