/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;

static class ServerHandshakeNetworkHandler.1 {
    static final /* synthetic */ int[] field_14155;

    static {
        field_14155 = new int[ConnectionIntent.values().length];
        try {
            ServerHandshakeNetworkHandler.1.field_14155[ConnectionIntent.LOGIN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerHandshakeNetworkHandler.1.field_14155[ConnectionIntent.STATUS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerHandshakeNetworkHandler.1.field_14155[ConnectionIntent.TRANSFER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
