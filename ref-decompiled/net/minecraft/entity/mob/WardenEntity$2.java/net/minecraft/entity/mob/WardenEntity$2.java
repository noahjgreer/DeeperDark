/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityPose;

static class WardenEntity.2 {
    static final /* synthetic */ int[] field_38170;

    static {
        field_38170 = new int[EntityPose.values().length];
        try {
            WardenEntity.2.field_38170[EntityPose.EMERGING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WardenEntity.2.field_38170[EntityPose.DIGGING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WardenEntity.2.field_38170[EntityPose.ROARING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WardenEntity.2.field_38170[EntityPose.SNIFFING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
