/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

static class SnifferBrain.SearchingTask
extends MultiTickTask<SnifferEntity> {
    SnifferBrain.SearchingTask() {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryModuleState.VALUE_PRESENT), 600);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
        return snifferEntity.canTryToDig();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        if (!snifferEntity.canTryToDig()) {
            snifferEntity.startState(SnifferEntity.State.IDLING);
            return false;
        }
        Optional<BlockPos> optional = snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET).map(WalkTarget::getLookTarget).map(LookTarget::getBlockPos);
        Optional<BlockPos> optional2 = snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
        if (optional.isEmpty() || optional2.isEmpty()) {
            return false;
        }
        return optional2.get().equals(optional.get());
    }

    @Override
    protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        snifferEntity.startState(SnifferEntity.State.SEARCHING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
        if (snifferEntity.canDig() && snifferEntity.canTryToDig()) {
            snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_DIGGING, true);
        }
        snifferEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
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
