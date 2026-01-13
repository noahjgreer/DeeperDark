/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function3
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Function3;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

class TaskTriggerer.TaskContext.4
implements TaskTriggerer.TaskFunction<E, R> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_41012;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41013;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41014;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41015;

    TaskTriggerer.TaskContext.4(TaskTriggerer.TaskContext taskContext, TaskTriggerer.TaskFunction taskFunction, TaskTriggerer.TaskFunction taskFunction2, TaskTriggerer.TaskFunction taskFunction3, TaskTriggerer.TaskFunction taskFunction4) {
        this.field_41012 = taskFunction;
        this.field_41013 = taskFunction2;
        this.field_41014 = taskFunction3;
        this.field_41015 = taskFunction4;
    }

    @Override
    public R run(ServerWorld world, E entity, long time) {
        Object object = this.field_41012.run(world, entity, time);
        if (object == null) {
            return null;
        }
        Object object2 = this.field_41013.run(world, entity, time);
        if (object2 == null) {
            return null;
        }
        Object object3 = this.field_41014.run(world, entity, time);
        if (object3 == null) {
            return null;
        }
        Function3 function3 = (Function3)this.field_41015.run(world, entity, time);
        if (function3 == null) {
            return null;
        }
        return function3.apply(object, object2, object3);
    }

    @Override
    public String asString() {
        return this.field_41015.asString() + " * " + this.field_41012.asString() + " * " + this.field_41013.asString() + " * " + this.field_41014.asString();
    }

    public String toString() {
        return this.asString();
    }
}
