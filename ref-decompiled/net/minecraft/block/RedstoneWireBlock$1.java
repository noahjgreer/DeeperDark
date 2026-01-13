/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

static class RedstoneWireBlock.1 {
    static final /* synthetic */ int[] field_24467;
    static final /* synthetic */ int[] field_11442;
    static final /* synthetic */ int[] field_11441;

    static {
        field_11441 = new int[BlockMirror.values().length];
        try {
            RedstoneWireBlock.1.field_11441[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RedstoneWireBlock.1.field_11441[BlockMirror.FRONT_BACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11442 = new int[BlockRotation.values().length];
        try {
            RedstoneWireBlock.1.field_11442[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RedstoneWireBlock.1.field_11442[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RedstoneWireBlock.1.field_11442[BlockRotation.CLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_24467 = new int[WireConnection.values().length];
        try {
            RedstoneWireBlock.1.field_24467[WireConnection.UP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RedstoneWireBlock.1.field_24467[WireConnection.SIDE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RedstoneWireBlock.1.field_24467[WireConnection.NONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
