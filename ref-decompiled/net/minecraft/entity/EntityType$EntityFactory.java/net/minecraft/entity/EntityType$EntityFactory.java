/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface EntityType.EntityFactory<T extends Entity> {
    public @Nullable T create(EntityType<T> var1, World var2);
}
