/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.util.BlockRotation;

static class TestContext.3 {
    static final /* synthetic */ int[] field_53733;

    static {
        field_53733 = new int[BlockRotation.values().length];
        try {
            TestContext.3.field_53733[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestContext.3.field_53733[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
