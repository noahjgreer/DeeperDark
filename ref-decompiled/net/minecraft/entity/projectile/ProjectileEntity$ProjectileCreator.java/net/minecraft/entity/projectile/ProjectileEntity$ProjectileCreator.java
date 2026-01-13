/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public static interface ProjectileEntity.ProjectileCreator<T extends ProjectileEntity> {
    public T create(ServerWorld var1, LivingEntity var2, ItemStack var3);
}
