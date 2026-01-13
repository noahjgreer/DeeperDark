/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.mob.ZombieEntity;

public static class HuskEntity.HuskData
extends ZombieEntity.ZombieData {
    public boolean unnatural = false;

    public HuskEntity.HuskData(ZombieEntity.ZombieData data) {
        super(data.baby, data.tryChickenJockey);
    }
}
