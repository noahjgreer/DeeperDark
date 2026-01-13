/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class Entity.1 {
    static final /* synthetic */ int[] field_6041;
    static final /* synthetic */ int[] field_6040;

    static {
        field_6040 = new int[BlockMirror.values().length];
        try {
            Entity.1.field_6040[BlockMirror.FRONT_BACK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Entity.1.field_6040[BlockMirror.LEFT_RIGHT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_6041 = new int[BlockRotation.values().length];
        try {
            Entity.1.field_6041[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Entity.1.field_6041[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Entity.1.field_6041[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
