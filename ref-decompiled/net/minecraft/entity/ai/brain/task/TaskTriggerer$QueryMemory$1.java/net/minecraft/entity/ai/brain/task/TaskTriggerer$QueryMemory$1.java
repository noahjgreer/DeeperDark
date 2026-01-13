/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;

static class TaskTriggerer.QueryMemory.1
implements TaskTriggerer.TaskFunction<E, MemoryQueryResult<F, Value>> {
    final /* synthetic */ MemoryQuery field_41023;

    TaskTriggerer.QueryMemory.1(MemoryQuery memoryQuery) {
        this.field_41023 = memoryQuery;
    }

    @Override
    public @Nullable MemoryQueryResult<F, Value> run(ServerWorld serverWorld, E livingEntity, long l) {
        Brain<?> brain = ((LivingEntity)livingEntity).getBrain();
        Optional optional = brain.getOptionalMemory(this.field_41023.memory());
        if (optional == null) {
            return null;
        }
        return this.field_41023.toQueryResult(brain, optional);
    }

    @Override
    public String asString() {
        return "M[" + String.valueOf(this.field_41023) + "]";
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public /* synthetic */ @Nullable Object run(ServerWorld world, LivingEntity entity, long time) {
        return this.run(world, entity, time);
    }
}
