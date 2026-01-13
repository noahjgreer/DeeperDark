/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;

@FunctionalInterface
public static interface Entity.PositionUpdater {
    public void accept(Entity var1, double var2, double var4, double var6);
}
