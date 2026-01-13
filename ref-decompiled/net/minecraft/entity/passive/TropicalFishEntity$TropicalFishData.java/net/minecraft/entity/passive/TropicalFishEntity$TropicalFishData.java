/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;

static class TropicalFishEntity.TropicalFishData
extends SchoolingFishEntity.FishData {
    final TropicalFishEntity.Variant variant;

    TropicalFishEntity.TropicalFishData(TropicalFishEntity leader, TropicalFishEntity.Variant variant) {
        super(leader);
        this.variant = variant;
    }
}
