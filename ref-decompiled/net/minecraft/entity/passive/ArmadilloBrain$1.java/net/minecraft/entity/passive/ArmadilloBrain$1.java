/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.server.world.ServerWorld;

static class ArmadilloBrain.1
extends MoveToTargetTask {
    ArmadilloBrain.1() {
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        ArmadilloEntity armadilloEntity;
        if (mobEntity instanceof ArmadilloEntity && (armadilloEntity = (ArmadilloEntity)mobEntity).isNotIdle()) {
            return false;
        }
        return super.shouldRun(serverWorld, mobEntity);
    }
}
