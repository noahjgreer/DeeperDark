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
import net.minecraft.entity.passive.ParrotEntity;

@Environment(value=EnvType.CLIENT)
static class ParrotEntityRenderer.1 {
    static final /* synthetic */ int[] field_41641;

    static {
        field_41641 = new int[ParrotEntity.Variant.values().length];
        try {
            ParrotEntityRenderer.1.field_41641[ParrotEntity.Variant.RED_BLUE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ParrotEntityRenderer.1.field_41641[ParrotEntity.Variant.BLUE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ParrotEntityRenderer.1.field_41641[ParrotEntity.Variant.GREEN.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ParrotEntityRenderer.1.field_41641[ParrotEntity.Variant.YELLOW_BLUE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ParrotEntityRenderer.1.field_41641[ParrotEntity.Variant.GRAY.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
