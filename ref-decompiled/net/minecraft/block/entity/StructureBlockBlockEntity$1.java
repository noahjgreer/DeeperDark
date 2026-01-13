/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class StructureBlockBlockEntity.1 {
    static final /* synthetic */ int[] field_4402;
    static final /* synthetic */ int[] field_4401;

    static {
        field_4401 = new int[BlockRotation.values().length];
        try {
            StructureBlockBlockEntity.1.field_4401[BlockRotation.CLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockBlockEntity.1.field_4401[BlockRotation.CLOCKWISE_180.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockBlockEntity.1.field_4401[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_4402 = new int[BlockMirror.values().length];
        try {
            StructureBlockBlockEntity.1.field_4402[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockBlockEntity.1.field_4402[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
