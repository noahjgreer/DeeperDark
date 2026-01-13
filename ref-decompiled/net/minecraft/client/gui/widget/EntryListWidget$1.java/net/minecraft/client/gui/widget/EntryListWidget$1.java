/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
static class EntryListWidget.1 {
    static final /* synthetic */ int[] field_41786;

    static {
        field_41786 = new int[NavigationDirection.values().length];
        try {
            EntryListWidget.1.field_41786[NavigationDirection.RIGHT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntryListWidget.1.field_41786[NavigationDirection.LEFT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntryListWidget.1.field_41786[NavigationDirection.UP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EntryListWidget.1.field_41786[NavigationDirection.DOWN.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
