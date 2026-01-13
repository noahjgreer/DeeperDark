/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.damage.DamageScaling;

static class DamageSource.1 {
    static final /* synthetic */ int[] field_42295;

    static {
        field_42295 = new int[DamageScaling.values().length];
        try {
            DamageSource.1.field_42295[DamageScaling.NEVER.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DamageSource.1.field_42295[DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DamageSource.1.field_42295[DamageScaling.ALWAYS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
