/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.server.world.ServerWorld;

static class SnifferBrain.2
extends BreedTask {
    SnifferBrain.2(EntityType entityType) {
        super(entityType);
    }

    @Override
    protected void run(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        SnifferBrain.stopDiggingOrSniffing((SnifferEntity)animalEntity);
        super.run(serverWorld, animalEntity, l);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (AnimalEntity)entity, time);
    }
}
