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
import net.minecraft.entity.passive.SalmonEntity;

@Environment(value=EnvType.CLIENT)
static class SalmonEntityRenderer.1 {
    static final /* synthetic */ int[] field_61802;

    static {
        field_61802 = new int[SalmonEntity.Variant.values().length];
        try {
            SalmonEntityRenderer.1.field_61802[SalmonEntity.Variant.SMALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SalmonEntityRenderer.1.field_61802[SalmonEntity.Variant.MEDIUM.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SalmonEntityRenderer.1.field_61802[SalmonEntity.Variant.LARGE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
