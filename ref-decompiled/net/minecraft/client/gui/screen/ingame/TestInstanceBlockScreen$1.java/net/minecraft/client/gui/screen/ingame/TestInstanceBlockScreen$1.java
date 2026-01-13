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
import net.minecraft.util.BlockRotation;

@Environment(value=EnvType.CLIENT)
static class TestInstanceBlockScreen.1 {
    static final /* synthetic */ int[] field_56059;

    static {
        field_56059 = new int[BlockRotation.values().length];
        try {
            TestInstanceBlockScreen.1.field_56059[BlockRotation.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceBlockScreen.1.field_56059[BlockRotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceBlockScreen.1.field_56059[BlockRotation.CLOCKWISE_180.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TestInstanceBlockScreen.1.field_56059[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
