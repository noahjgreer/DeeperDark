/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

static final class ServerLightingProvider.Stage
extends Enum<ServerLightingProvider.Stage> {
    public static final /* enum */ ServerLightingProvider.Stage PRE_UPDATE = new ServerLightingProvider.Stage();
    public static final /* enum */ ServerLightingProvider.Stage POST_UPDATE = new ServerLightingProvider.Stage();
    private static final /* synthetic */ ServerLightingProvider.Stage[] field_17263;

    public static ServerLightingProvider.Stage[] values() {
        return (ServerLightingProvider.Stage[])field_17263.clone();
    }

    public static ServerLightingProvider.Stage valueOf(String string) {
        return Enum.valueOf(ServerLightingProvider.Stage.class, string);
    }

    private static /* synthetic */ ServerLightingProvider.Stage[] method_36577() {
        return new ServerLightingProvider.Stage[]{PRE_UPDATE, POST_UPDATE};
    }

    static {
        field_17263 = ServerLightingProvider.Stage.method_36577();
    }
}
