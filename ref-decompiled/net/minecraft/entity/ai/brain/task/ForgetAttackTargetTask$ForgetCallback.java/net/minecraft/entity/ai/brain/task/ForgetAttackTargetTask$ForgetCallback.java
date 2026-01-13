/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface ForgetAttackTargetTask.ForgetCallback<E> {
    public void accept(ServerWorld var1, E var2, LivingEntity var3);
}
