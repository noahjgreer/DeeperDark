/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

class TaskTriggerer.TaskContext.1
implements TaskTriggerer.TaskFunction<E, R> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_41002;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41003;

    TaskTriggerer.TaskContext.1(TaskTriggerer.TaskContext taskContext, TaskTriggerer.TaskFunction taskFunction, TaskTriggerer.TaskFunction taskFunction2) {
        this.field_41002 = taskFunction;
        this.field_41003 = taskFunction2;
    }

    @Override
    public R run(ServerWorld world, E entity, long time) {
        Object object = this.field_41002.run(world, entity, time);
        if (object == null) {
            return null;
        }
        Function function = (Function)this.field_41003.run(world, entity, time);
        if (function == null) {
            return null;
        }
        return function.apply(object);
    }

    @Override
    public String asString() {
        return this.field_41003.asString() + " * " + this.field_41002.asString();
    }

    public String toString() {
        return this.asString();
    }
}
