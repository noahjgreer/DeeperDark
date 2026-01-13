/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.CopperGolemState;

static class CopperGolemEntity.1 {
    static final /* synthetic */ int[] field_61276;

    static {
        field_61276 = new int[CopperGolemState.values().length];
        try {
            CopperGolemEntity.1.field_61276[CopperGolemState.IDLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemEntity.1.field_61276[CopperGolemState.GETTING_ITEM.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemEntity.1.field_61276[CopperGolemState.GETTING_NO_ITEM.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemEntity.1.field_61276[CopperGolemState.DROPPING_ITEM.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemEntity.1.field_61276[CopperGolemState.DROPPING_NO_ITEM.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
