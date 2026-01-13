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

@Environment(value=EnvType.CLIENT)
static class ScreenRect.1 {
    static final /* synthetic */ int[] field_41836;

    static {
        field_41836 = new int[NavigationAxis.values().length];
        try {
            ScreenRect.1.field_41836[NavigationAxis.HORIZONTAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ScreenRect.1.field_41836[NavigationAxis.VERTICAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
