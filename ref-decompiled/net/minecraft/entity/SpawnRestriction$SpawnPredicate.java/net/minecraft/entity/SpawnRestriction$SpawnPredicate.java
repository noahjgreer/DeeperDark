/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

@FunctionalInterface
public static interface SpawnRestriction.SpawnPredicate<T extends Entity> {
    public boolean test(EntityType<T> var1, ServerWorldAccess var2, SpawnReason var3, BlockPos var4, Random var5);
}
