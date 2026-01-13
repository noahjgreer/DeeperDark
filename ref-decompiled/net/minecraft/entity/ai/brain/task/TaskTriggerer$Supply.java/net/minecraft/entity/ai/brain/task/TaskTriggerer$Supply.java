/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;

static final class TaskTriggerer.Supply<E extends LivingEntity, A>
extends TaskTriggerer<E, A> {
    TaskTriggerer.Supply(A value) {
        this(value, () -> "C[" + String.valueOf(value) + "]");
    }

    TaskTriggerer.Supply(final A value, final Supplier<String> nameSupplier) {
        super(new TaskTriggerer.TaskFunction<E, A>(){

            @Override
            public A run(ServerWorld world, E entity, long time) {
                return value;
            }

            @Override
            public String asString() {
                return (String)nameSupplier.get();
            }

            public String toString() {
                return this.asString();
            }
        });
    }
}
