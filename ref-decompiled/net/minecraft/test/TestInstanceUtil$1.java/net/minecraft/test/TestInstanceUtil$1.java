/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.util.BlockRotation;

static class TestInstanceUtil.1 {
    static final /* synthetic */ int[] field_33175;

    static {
        field_33175 = new int[BlockRotation.values().length];
        try {
            TestInstanceUtil.1.field_33175[BlockRotation.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceUtil.1.field_33175[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceUtil.1.field_33175[BlockRotation.CLOCKWISE_180.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceUtil.1.field_33175[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
