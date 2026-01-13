/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class StructureTemplate.1 {
    static final /* synthetic */ int[] field_15594;
    static final /* synthetic */ int[] field_15593;

    static {
        field_15593 = new int[BlockMirror.values().length];
        try {
            StructureTemplate.1.field_15593[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureTemplate.1.field_15593[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_15594 = new int[BlockRotation.values().length];
        try {
            StructureTemplate.1.field_15594[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureTemplate.1.field_15594[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureTemplate.1.field_15594[BlockRotation.CLOCKWISE_180.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureTemplate.1.field_15594[BlockRotation.NONE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
