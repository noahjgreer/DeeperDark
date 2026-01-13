/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Unit
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Unit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.TaskRunnable;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;

static final class TaskTriggerer.Trigger<E extends LivingEntity>
extends TaskTriggerer<E, Unit> {
    TaskTriggerer.Trigger(final TaskRunnable<? super E> taskRunnable) {
        super(new TaskTriggerer.TaskFunction<E, Unit>(){

            @Override
            public @Nullable Unit run(ServerWorld serverWorld, E livingEntity, long l) {
                return taskRunnable.trigger(serverWorld, livingEntity, l) ? Unit.INSTANCE : null;
            }

            @Override
            public String asString() {
                return "T[" + String.valueOf(taskRunnable) + "]";
            }

            @Override
            public /* synthetic */ @Nullable Object run(ServerWorld world, LivingEntity entity, long time) {
                return this.run(world, (Object)entity, time);
            }
        });
    }
}
