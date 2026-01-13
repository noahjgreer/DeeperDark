/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.server.world.ServerWorld;

public static class CamelBrain.SitOrStandTask
extends MultiTickTask<CamelEntity> {
    private final int lastTimeSinceLastPoseTick;

    public CamelBrain.SitOrStandTask(int lastPoseSecondsDelta) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of());
        this.lastTimeSinceLastPoseTick = lastPoseSecondsDelta * 20;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, CamelEntity camelEntity) {
        return !camelEntity.isTouchingWater() && camelEntity.getTimeSinceLastPoseTick() >= (long)this.lastTimeSinceLastPoseTick && !camelEntity.isLeashed() && camelEntity.isOnGround() && !camelEntity.hasControllingPassenger() && camelEntity.canChangePose();
    }

    @Override
    protected void run(ServerWorld serverWorld, CamelEntity camelEntity, long l) {
        if (camelEntity.isSitting()) {
            camelEntity.startStanding();
        } else if (!camelEntity.isPanicking()) {
            camelEntity.startSitting();
        }
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (CamelEntity)entity, time);
    }
}
