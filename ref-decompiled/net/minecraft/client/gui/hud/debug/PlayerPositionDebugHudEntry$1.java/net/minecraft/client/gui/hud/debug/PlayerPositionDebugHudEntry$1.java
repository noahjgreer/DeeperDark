/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static class PlayerPositionDebugHudEntry.1 {
    static final /* synthetic */ int[] field_61551;

    static {
        field_61551 = new int[Direction.values().length];
        try {
            PlayerPositionDebugHudEntry.1.field_61551[Direction.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerPositionDebugHudEntry.1.field_61551[Direction.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerPositionDebugHudEntry.1.field_61551[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerPositionDebugHudEntry.1.field_61551[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
