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

static class SnifferBrain.FinishDiggingTask
extends MultiTickTask<SnifferEntity> {
    SnifferBrain.FinishDiggingTask(int runTime) {
        super(Map.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_DIGGING, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleState.VALUE_PRESENT), runTime, runTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
        return true;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        return snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_DIGGING).isPresent();
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.RISING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        boolean bl = this.isTimeLimitExceeded(l);
        snifferEntity.startState(SnifferEntity.State.IDLING).finishDigging(bl);
        snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_DIGGING);
        snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_HAPPY, true);
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
