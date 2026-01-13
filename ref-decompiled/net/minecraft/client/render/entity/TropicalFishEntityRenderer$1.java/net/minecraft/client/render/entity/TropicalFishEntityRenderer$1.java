/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
static class TropicalFishEntityRenderer.1 {
    static final /* synthetic */ int[] field_41645;

    static {
        field_41645 = new int[TropicalFishEntity.Size.values().length];
        try {
            TropicalFishEntityRenderer.1.field_41645[TropicalFishEntity.Size.SMALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishEntityRenderer.1.field_41645[TropicalFishEntity.Size.LARGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
