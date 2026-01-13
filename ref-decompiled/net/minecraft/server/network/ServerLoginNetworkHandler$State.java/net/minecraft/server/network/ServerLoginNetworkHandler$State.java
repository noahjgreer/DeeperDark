/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

static final class ServerLoginNetworkHandler.State
extends Enum<ServerLoginNetworkHandler.State> {
    public static final /* enum */ ServerLoginNetworkHandler.State HELLO = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State KEY = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State AUTHENTICATING = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State NEGOTIATING = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State VERIFYING = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State WAITING_FOR_DUPE_DISCONNECT = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State PROTOCOL_SWITCHING = new ServerLoginNetworkHandler.State();
    public static final /* enum */ ServerLoginNetworkHandler.State ACCEPTED = new ServerLoginNetworkHandler.State();
    private static final /* synthetic */ ServerLoginNetworkHandler.State[] field_14174;

    public static ServerLoginNetworkHandler.State[] values() {
        return (ServerLoginNetworkHandler.State[])field_14174.clone();
    }

    public static ServerLoginNetworkHandler.State valueOf(String string) {
        return Enum.valueOf(ServerLoginNetworkHandler.State.class, string);
    }

    private static /* synthetic */ ServerLoginNetworkHandler.State[] method_36581() {
        return new ServerLoginNetworkHandler.State[]{HELLO, KEY, AUTHENTICATING, NEGOTIATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED};
    }

    static {
        field_14174 = ServerLoginNetworkHandler.State.method_36581();
    }
}
