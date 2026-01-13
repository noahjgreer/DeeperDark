/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
static class WorldLoadedEvent.1 {
    static final /* synthetic */ int[] field_34955;

    static {
        field_34955 = new int[GameMode.values().length];
        try {
            WorldLoadedEvent.1.field_34955[GameMode.SURVIVAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WorldLoadedEvent.1.field_34955[GameMode.CREATIVE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WorldLoadedEvent.1.field_34955[GameMode.ADVENTURE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            WorldLoadedEvent.1.field_34955[GameMode.SPECTATOR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
