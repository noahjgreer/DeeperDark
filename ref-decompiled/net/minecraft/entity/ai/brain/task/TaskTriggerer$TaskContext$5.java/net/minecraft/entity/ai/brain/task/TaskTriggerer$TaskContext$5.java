/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function4
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Function4;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

class TaskTriggerer.TaskContext.5
implements TaskTriggerer.TaskFunction<E, R> {
    final /* synthetic */ TaskTriggerer.TaskFunction field_41017;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41018;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41019;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41020;
    final /* synthetic */ TaskTriggerer.TaskFunction field_41021;

    TaskTriggerer.TaskContext.5(TaskTriggerer.TaskContext taskContext, TaskTriggerer.TaskFunction taskFunction, TaskTriggerer.TaskFunction taskFunction2, TaskTriggerer.TaskFunction taskFunction3, TaskTriggerer.TaskFunction taskFunction4, TaskTriggerer.TaskFunction taskFunction5) {
        this.field_41017 = taskFunction;
        this.field_41018 = taskFunction2;
        this.field_41019 = taskFunction3;
        this.field_41020 = taskFunction4;
        this.field_41021 = taskFunction5;
    }

    @Override
    public R run(ServerWorld world, E entity, long time) {
        Object object = this.field_41017.run(world, entity, time);
        if (object == null) {
            return null;
        }
        Object object2 = this.field_41018.run(world, entity, time);
        if (object2 == null) {
            return null;
        }
        Object object3 = this.field_41019.run(world, entity, time);
        if (object3 == null) {
            return null;
        }
        Object object4 = this.field_41020.run(world, entity, time);
        if (object4 == null) {
            return null;
        }
        Function4 function4 = (Function4)this.field_41021.run(world, entity, time);
        if (function4 == null) {
            return null;
        }
        return function4.apply(object, object2, object3, object4);
    }

    @Override
    public String asString() {
        return this.field_41021.asString() + " * " + this.field_41017.asString() + " * " + this.field_41018.asString() + " * " + this.field_41019.asString() + " * " + this.field_41020.asString();
    }

    public String toString() {
        return this.asString();
    }
}
