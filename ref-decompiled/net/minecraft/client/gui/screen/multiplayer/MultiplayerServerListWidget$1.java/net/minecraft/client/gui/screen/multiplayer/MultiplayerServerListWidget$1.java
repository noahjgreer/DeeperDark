/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;

@Environment(value=EnvType.CLIENT)
static class MultiplayerServerListWidget.1 {
    static final /* synthetic */ int[] field_47851;

    static {
        field_47851 = new int[ServerInfo.Status.values().length];
        try {
            MultiplayerServerListWidget.1.field_47851[ServerInfo.Status.INITIAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiplayerServerListWidget.1.field_47851[ServerInfo.Status.PINGING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiplayerServerListWidget.1.field_47851[ServerInfo.Status.INCOMPATIBLE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiplayerServerListWidget.1.field_47851[ServerInfo.Status.UNREACHABLE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MultiplayerServerListWidget.1.field_47851[ServerInfo.Status.SUCCESSFUL.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
