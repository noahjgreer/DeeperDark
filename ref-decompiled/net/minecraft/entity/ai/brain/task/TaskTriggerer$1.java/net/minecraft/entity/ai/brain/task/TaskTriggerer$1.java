/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskRunnable;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

static class TaskTriggerer.1
extends SingleTickTask<E> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_40999;

    TaskTriggerer.1(TaskTriggerer.TaskFunction taskFunction) {
        this.field_40999 = taskFunction;
    }

    @Override
    public boolean trigger(ServerWorld serverWorld, E livingEntity, long l) {
        TaskRunnable taskRunnable = (TaskRunnable)this.field_40999.run(serverWorld, livingEntity, l);
        if (taskRunnable == null) {
            return false;
        }
        return taskRunnable.trigger(serverWorld, livingEntity, l);
    }

    @Override
    public String getName() {
        return "OneShot[" + this.field_40999.asString() + "]";
    }

    public String toString() {
        return this.getName();
    }
}
