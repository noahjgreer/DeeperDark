/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.conversion;

import net.minecraft.entity.mob.MobEntity;

@FunctionalInterface
public static interface EntityConversionContext.Finalizer<T extends MobEntity> {
    public void finalizeConversion(T var1);
}
