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
import net.minecraft.entity.passive.RabbitEntity;

@Environment(value=EnvType.CLIENT)
static class RabbitEntityRenderer.1 {
    static final /* synthetic */ int[] field_41642;

    static {
        field_41642 = new int[RabbitEntity.Variant.values().length];
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.BROWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.WHITE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.BLACK.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.GOLD.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.SALT.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.WHITE_SPLOTCHED.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RabbitEntityRenderer.1.field_41642[RabbitEntity.Variant.EVIL.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
