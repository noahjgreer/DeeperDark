/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;

public static class ArmadilloBrain.UnrollAndFleeTask
extends FleeTask<ArmadilloEntity> {
    public ArmadilloBrain.UnrollAndFleeTask(float f) {
        super(f, entity -> DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
    }

    @Override
    protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
        armadilloEntity.unroll();
        super.run(serverWorld, armadilloEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        this.run(serverWorld, (ArmadilloEntity)pathAwareEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (ArmadilloEntity)entity, time);
    }
}
