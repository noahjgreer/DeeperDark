/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

static class SnifferBrain.SniffingTask
extends MultiTickTask<SnifferEntity> {
    SnifferBrain.SniffingTask(int minRunTime, int maxRunTime) {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleState.VALUE_ABSENT), minRunTime, maxRunTime);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
        return !snifferEntity.isBaby() && snifferEntity.canTryToDig();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        return snifferEntity.canTryToDig();
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.SNIFFING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        boolean bl = this.isTimeLimitExceeded(l);
        snifferEntity.startState(SnifferEntity.State.IDLING);
        if (bl) {
            snifferEntity.findSniffingTargetPos().ifPresent(pos -> {
                snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_SNIFFING_TARGET, pos);
                snifferEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)pos, 1.25f, 0));
            });
        }
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
