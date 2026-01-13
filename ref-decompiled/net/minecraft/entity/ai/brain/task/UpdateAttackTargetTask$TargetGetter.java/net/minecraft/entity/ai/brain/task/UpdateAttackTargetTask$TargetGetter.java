/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface UpdateAttackTargetTask.TargetGetter<E> {
    public Optional<? extends LivingEntity> get(ServerWorld var1, E var2);
}
