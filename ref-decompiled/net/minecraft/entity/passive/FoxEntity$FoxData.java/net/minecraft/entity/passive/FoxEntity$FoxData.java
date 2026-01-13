/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;

public static class FoxEntity.FoxData
extends PassiveEntity.PassiveData {
    public final FoxEntity.Variant type;

    public FoxEntity.FoxData(FoxEntity.Variant type) {
        super(false);
        this.type = type;
    }
}
