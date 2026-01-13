/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

static final class ServerEntityManager.Status
extends Enum<ServerEntityManager.Status> {
    public static final /* enum */ ServerEntityManager.Status FRESH = new ServerEntityManager.Status();
    public static final /* enum */ ServerEntityManager.Status PENDING = new ServerEntityManager.Status();
    public static final /* enum */ ServerEntityManager.Status LOADED = new ServerEntityManager.Status();
    private static final /* synthetic */ ServerEntityManager.Status[] field_27278;

    public static ServerEntityManager.Status[] values() {
        return (ServerEntityManager.Status[])field_27278.clone();
    }

    public static ServerEntityManager.Status valueOf(String string) {
        return Enum.valueOf(ServerEntityManager.Status.class, string);
    }

    private static /* synthetic */ ServerEntityManager.Status[] method_36746() {
        return new ServerEntityManager.Status[]{FRESH, PENDING, LOADED};
    }

    static {
        field_27278 = ServerEntityManager.Status.method_36746();
    }
}
