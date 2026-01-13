/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EquipmentSlot;

static class MobEntity.2 {
    static final /* synthetic */ int[] field_55956;

    static {
        field_55956 = new int[EquipmentSlot.values().length];
        try {
            MobEntity.2.field_55956[EquipmentSlot.HEAD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MobEntity.2.field_55956[EquipmentSlot.CHEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MobEntity.2.field_55956[EquipmentSlot.LEGS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MobEntity.2.field_55956[EquipmentSlot.FEET.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
