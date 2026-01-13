/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

static class NetherPortalBlock.1 {
    static final /* synthetic */ int[] field_11320;
    static final /* synthetic */ int[] field_11319;

    static {
        field_11319 = new int[BlockRotation.values().length];
        try {
            NetherPortalBlock.1.field_11319[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetherPortalBlock.1.field_11319[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_11320 = new int[Direction.Axis.values().length];
        try {
            NetherPortalBlock.1.field_11320[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NetherPortalBlock.1.field_11320[Direction.Axis.Z.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
