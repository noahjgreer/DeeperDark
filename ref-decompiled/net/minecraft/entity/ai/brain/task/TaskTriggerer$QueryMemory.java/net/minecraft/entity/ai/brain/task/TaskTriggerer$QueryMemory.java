/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.K1
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.kinds.K1;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryQuery;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;

static final class TaskTriggerer.QueryMemory<E extends LivingEntity, F extends K1, Value>
extends TaskTriggerer<E, MemoryQueryResult<F, Value>> {
    TaskTriggerer.QueryMemory(final MemoryQuery<F, Value> query) {
        super(new TaskTriggerer.TaskFunction<E, MemoryQueryResult<F, Value>>(){

            @Override
            public @Nullable MemoryQueryResult<F, Value> run(ServerWorld serverWorld, E livingEntity, long l) {
                Brain<?> brain = ((LivingEntity)livingEntity).getBrain();
                Optional optional = brain.getOptionalMemory(query.memory());
                if (optional == null) {
                    return null;
                }
                return query.toQueryResult(brain, optional);
            }

            @Override
            public String asString() {
                return "M[" + String.valueOf(query) + "]";
            }

            public String toString() {
                return this.asString();
            }

            @Override
            public /* synthetic */ @Nullable Object run(ServerWorld world, LivingEntity entity, long time) {
                return this.run(world, entity, time);
            }
        });
    }
}
