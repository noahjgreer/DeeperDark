/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Supplier;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

static class TaskTriggerer.Supply.1
implements TaskTriggerer.TaskFunction<E, A> {
    final /* synthetic */ Object field_41000;
    final /* synthetic */ Supplier field_41001;

    TaskTriggerer.Supply.1(Object object, Supplier supplier) {
        this.field_41000 = object;
        this.field_41001 = supplier;
    }

    @Override
    public A run(ServerWorld world, E entity, long time) {
        return this.field_41000;
    }

    @Override
    public String asString() {
        return (String)this.field_41001.get();
    }

    public String toString() {
        return this.asString();
    }
}
