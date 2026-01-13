/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

@Environment(value=EnvType.CLIENT)
static class ClientPlayNetworkHandler.3 {
    static final /* synthetic */ int[] field_60785;

    static {
        field_60785 = new int[PlayerListS2CPacket.Action.values().length];
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.INITIALIZE_CHAT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_GAME_MODE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_LISTED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_LATENCY.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_HAT.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientPlayNetworkHandler.3.field_60785[PlayerListS2CPacket.Action.UPDATE_LIST_ORDER.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
