/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.enums.Attachment;

static class BellBlock.1 {
    static final /* synthetic */ int[] field_16327;

    static {
        field_16327 = new int[Attachment.values().length];
        try {
            BellBlock.1.field_16327[Attachment.FLOOR.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BellBlock.1.field_16327[Attachment.SINGLE_WALL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BellBlock.1.field_16327[Attachment.DOUBLE_WALL.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BellBlock.1.field_16327[Attachment.CEILING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
