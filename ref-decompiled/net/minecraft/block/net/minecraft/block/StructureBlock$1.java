/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.StructureBlockMode;

static class StructureBlock.1 {
    static final /* synthetic */ int[] field_11587;

    static {
        field_11587 = new int[StructureBlockMode.values().length];
        try {
            StructureBlock.1.field_11587[StructureBlockMode.SAVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlock.1.field_11587[StructureBlockMode.LOAD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlock.1.field_11587[StructureBlockMode.CORNER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlock.1.field_11587[StructureBlockMode.DATA.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
