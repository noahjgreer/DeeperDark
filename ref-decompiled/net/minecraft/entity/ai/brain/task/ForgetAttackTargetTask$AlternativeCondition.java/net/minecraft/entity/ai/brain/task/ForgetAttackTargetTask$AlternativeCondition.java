/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface ForgetAttackTargetTask.AlternativeCondition {
    public boolean test(ServerWorld var1, LivingEntity var2);
}
