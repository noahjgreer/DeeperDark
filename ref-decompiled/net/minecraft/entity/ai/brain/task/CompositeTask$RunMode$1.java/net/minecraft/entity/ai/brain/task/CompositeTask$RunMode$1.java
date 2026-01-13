/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

final class CompositeTask.RunMode.1
extends CompositeTask.RunMode {
    @Override
    public <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
        tasks.filter(task -> task.getStatus() == MultiTickTask.Status.STOPPED).filter(task -> task.tryStarting(world, entity, time)).findFirst();
    }
}
