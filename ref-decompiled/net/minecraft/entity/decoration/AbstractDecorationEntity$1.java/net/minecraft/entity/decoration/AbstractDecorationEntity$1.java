/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import net.minecraft.util.BlockRotation;

static class AbstractDecorationEntity.1 {
    static final /* synthetic */ int[] field_7101;

    static {
        field_7101 = new int[BlockRotation.values().length];
        try {
            AbstractDecorationEntity.1.field_7101[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractDecorationEntity.1.field_7101[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractDecorationEntity.1.field_7101[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
