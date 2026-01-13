/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
static class ScreenPos.1 {
    static final /* synthetic */ int[] field_41833;
    static final /* synthetic */ int[] field_41834;

    static {
        field_41834 = new int[NavigationDirection.values().length];
        try {
            ScreenPos.1.field_41834[NavigationDirection.DOWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ScreenPos.1.field_41834[NavigationDirection.UP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ScreenPos.1.field_41834[NavigationDirection.LEFT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ScreenPos.1.field_41834[NavigationDirection.RIGHT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_41833 = new int[NavigationAxis.values().length];
        try {
            ScreenPos.1.field_41833[NavigationAxis.HORIZONTAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ScreenPos.1.field_41833[NavigationAxis.VERTICAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
