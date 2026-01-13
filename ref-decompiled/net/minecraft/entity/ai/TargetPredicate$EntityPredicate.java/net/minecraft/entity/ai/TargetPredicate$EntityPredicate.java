/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface TargetPredicate.EntityPredicate {
    public boolean test(LivingEntity var1, ServerWorld var2);
}
