/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Function;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

class TaskTriggerer.TaskContext.2
implements TaskTriggerer.TaskFunction<E, R> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_41005;
    final /* synthetic */ Function field_41006;

    TaskTriggerer.TaskContext.2(TaskTriggerer.TaskContext taskContext, TaskTriggerer.TaskFunction taskFunction, Function function) {
        this.field_41005 = taskFunction;
        this.field_41006 = function;
    }

    @Override
    public R run(ServerWorld world, E entity, long time) {
        Object object = this.field_41005.run(world, entity, time);
        if (object == null) {
            return null;
        }
        return this.field_41006.apply(object);
    }

    @Override
    public String asString() {
        return this.field_41005.asString() + ".map[" + String.valueOf(this.field_41006) + "]";
    }

    public String toString() {
        return this.asString();
    }
}
