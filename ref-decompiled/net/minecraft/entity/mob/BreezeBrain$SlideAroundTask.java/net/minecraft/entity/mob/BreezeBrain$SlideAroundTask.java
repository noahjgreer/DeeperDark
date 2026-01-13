/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

public static class BreezeBrain.SlideAroundTask
extends MoveToTargetTask {
    @VisibleForTesting
    public BreezeBrain.SlideAroundTask(int i, int j) {
        super(i, j);
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        super.run(serverWorld, mobEntity, l);
        mobEntity.playSoundIfNotSilent(SoundEvents.ENTITY_BREEZE_SLIDE);
        mobEntity.setPose(EntityPose.SLIDING);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        super.finishRunning(serverWorld, mobEntity, l);
        mobEntity.setPose(EntityPose.STANDING);
        if (mobEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            mobEntity.getBrain().remember(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
        }
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (MobEntity)entity, time);
    }
}
