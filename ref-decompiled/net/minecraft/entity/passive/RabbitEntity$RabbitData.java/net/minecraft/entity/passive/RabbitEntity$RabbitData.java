/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;

public static class RabbitEntity.RabbitData
extends PassiveEntity.PassiveData {
    public final RabbitEntity.Variant variant;

    public RabbitEntity.RabbitData(RabbitEntity.Variant variant) {
        super(1.0f);
        this.variant = variant;
    }
}
