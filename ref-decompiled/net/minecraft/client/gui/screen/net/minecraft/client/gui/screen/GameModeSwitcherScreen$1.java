/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
static class GameModeSwitcherScreen.1 {
    static final /* synthetic */ int[] field_24575;

    static {
        field_24575 = new int[GameMode.values().length];
        try {
            GameModeSwitcherScreen.1.field_24575[GameMode.SPECTATOR.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameModeSwitcherScreen.1.field_24575[GameMode.SURVIVAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameModeSwitcherScreen.1.field_24575[GameMode.CREATIVE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameModeSwitcherScreen.1.field_24575[GameMode.ADVENTURE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
