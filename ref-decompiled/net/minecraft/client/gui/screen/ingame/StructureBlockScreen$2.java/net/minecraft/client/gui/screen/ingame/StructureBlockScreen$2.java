/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.util.BlockRotation;

@Environment(value=EnvType.CLIENT)
static class StructureBlockScreen.2 {
    static final /* synthetic */ int[] field_3025;
    static final /* synthetic */ int[] field_3024;

    static {
        field_3024 = new int[StructureBlockMode.values().length];
        try {
            StructureBlockScreen.2.field_3024[StructureBlockMode.SAVE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3024[StructureBlockMode.LOAD.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3024[StructureBlockMode.CORNER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3024[StructureBlockMode.DATA.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_3025 = new int[BlockRotation.values().length];
        try {
            StructureBlockScreen.2.field_3025[BlockRotation.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3025[BlockRotation.CLOCKWISE_180.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3025[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            StructureBlockScreen.2.field_3025[BlockRotation.CLOCKWISE_90.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
