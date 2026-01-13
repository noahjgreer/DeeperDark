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
static class ConnectScreen.2 {
    static final /* synthetic */ int[] field_47591;

    static {
        field_47591 = new int[ServerInfo.ResourcePackPolicy.values().length];
        try {
            ConnectScreen.2.field_47591[ServerInfo.ResourcePackPolicy.ENABLED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ConnectScreen.2.field_47591[ServerInfo.ResourcePackPolicy.DISABLED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ConnectScreen.2.field_47591[ServerInfo.ResourcePackPolicy.PROMPT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
