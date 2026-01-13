/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;

static interface TaskTriggerer.TaskFunction<E extends LivingEntity, R> {
    public @Nullable R run(ServerWorld var1, E var2, long var3);

    public String asString();
}
