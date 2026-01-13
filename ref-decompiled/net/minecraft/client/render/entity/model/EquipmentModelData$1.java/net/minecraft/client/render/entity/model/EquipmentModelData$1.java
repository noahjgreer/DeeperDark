/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;

@Environment(value=EnvType.CLIENT)
static class EquipmentModelData.1 {
    static final /* synthetic */ int[] field_61795;

    static {
        field_61795 = new int[EquipmentSlot.values().length];
        try {
            EquipmentModelData.1.field_61795[EquipmentSlot.HEAD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EquipmentModelData.1.field_61795[EquipmentSlot.CHEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EquipmentModelData.1.field_61795[EquipmentSlot.LEGS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EquipmentModelData.1.field_61795[EquipmentSlot.FEET.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
