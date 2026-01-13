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
import net.minecraft.entity.decoration.DisplayEntity;

@Environment(value=EnvType.CLIENT)
static class DisplayEntityRenderer.1 {
    static final /* synthetic */ int[] field_42526;
    static final /* synthetic */ int[] field_42527;

    static {
        field_42527 = new int[DisplayEntity.TextDisplayEntity.TextAlignment.values().length];
        try {
            DisplayEntityRenderer.1.field_42527[DisplayEntity.TextDisplayEntity.TextAlignment.LEFT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DisplayEntityRenderer.1.field_42527[DisplayEntity.TextDisplayEntity.TextAlignment.RIGHT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DisplayEntityRenderer.1.field_42527[DisplayEntity.TextDisplayEntity.TextAlignment.CENTER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_42526 = new int[DisplayEntity.BillboardMode.values().length];
        try {
            DisplayEntityRenderer.1.field_42526[DisplayEntity.BillboardMode.FIXED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DisplayEntityRenderer.1.field_42526[DisplayEntity.BillboardMode.HORIZONTAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DisplayEntityRenderer.1.field_42526[DisplayEntity.BillboardMode.VERTICAL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DisplayEntityRenderer.1.field_42526[DisplayEntity.BillboardMode.CENTER.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
