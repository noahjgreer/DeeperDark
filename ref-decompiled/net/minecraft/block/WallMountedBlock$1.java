/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.BlockFace;

static class WallMountedBlock.1 {
    static final /* synthetic */ int[] field_11008;

    static {
        field_11008 = new int[BlockFace.values().length];
        try {
            WallMountedBlock.1.field_11008[BlockFace.CEILING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WallMountedBlock.1.field_11008[BlockFace.FLOOR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
