/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface UpdateAttackTargetTask.StartCondition<E> {
    public boolean test(ServerWorld var1, E var2);
}
