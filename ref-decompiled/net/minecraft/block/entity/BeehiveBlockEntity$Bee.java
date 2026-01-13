/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BeehiveBlockEntity;

static class BeehiveBlockEntity.Bee {
    private final BeehiveBlockEntity.BeeData data;
    private int ticksInHive;

    BeehiveBlockEntity.Bee(BeehiveBlockEntity.BeeData data) {
        this.data = data;
        this.ticksInHive = data.ticksInHive();
    }

    public boolean canExitHive() {
        return this.ticksInHive++ > this.data.minTicksInHive;
    }

    public BeehiveBlockEntity.BeeData createData() {
        return new BeehiveBlockEntity.BeeData(this.data.entityData, this.ticksInHive, this.data.minTicksInHive);
    }

    public boolean hasNectar() {
        return this.data.entityData.getNbtWithoutId().getBoolean("HasNectar", false);
    }
}
