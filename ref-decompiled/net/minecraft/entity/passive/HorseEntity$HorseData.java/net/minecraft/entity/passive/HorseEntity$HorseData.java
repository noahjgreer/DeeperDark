/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.PassiveEntity;

public static class HorseEntity.HorseData
extends PassiveEntity.PassiveData {
    public final HorseColor color;

    public HorseEntity.HorseData(HorseColor color) {
        super(true);
        this.color = color;
    }
}
