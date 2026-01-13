/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.BiFunction;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

class TaskTriggerer.TaskContext.3
implements TaskTriggerer.TaskFunction<E, R> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_41008;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41009;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41010;

    TaskTriggerer.TaskContext.3(TaskTriggerer.TaskContext taskContext, TaskTriggerer.TaskFunction taskFunction, TaskTriggerer.TaskFunction taskFunction2, TaskTriggerer.TaskFunction taskFunction3) {
        this.field_41008 = taskFunction;
        this.field_41009 = taskFunction2;
        this.field_41010 = taskFunction3;
    }

    @Override
    public R run(ServerWorld world, E entity, long time) {
        Object object = this.field_41008.run(world, entity, time);
        if (object == null) {
            return null;
        }
        Object object2 = this.field_41009.run(world, entity, time);
        if (object2 == null) {
            return null;
        }
        BiFunction biFunction = (BiFunction)this.field_41010.run(world, entity, time);
        if (biFunction == null) {
            return null;
        }
        return biFunction.apply(object, object2);
    }

    @Override
    public String asString() {
        return this.field_41010.asString() + " * " + this.field_41008.asString() + " * " + this.field_41009.asString();
    }

    public String toString() {
        return this.asString();
    }
}
