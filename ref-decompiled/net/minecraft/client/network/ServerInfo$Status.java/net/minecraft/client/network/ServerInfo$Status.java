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

@Environment(value=EnvType.CLIENT)
public static final class ServerInfo.Status
extends Enum<ServerInfo.Status> {
    public static final /* enum */ ServerInfo.Status INITIAL = new ServerInfo.Status();
    public static final /* enum */ ServerInfo.Status PINGING = new ServerInfo.Status();
    public static final /* enum */ ServerInfo.Status UNREACHABLE = new ServerInfo.Status();
    public static final /* enum */ ServerInfo.Status INCOMPATIBLE = new ServerInfo.Status();
    public static final /* enum */ ServerInfo.Status SUCCESSFUL = new ServerInfo.Status();
    private static final /* synthetic */ ServerInfo.Status[] field_47885;

    public static ServerInfo.Status[] values() {
        return (ServerInfo.Status[])field_47885.clone();
    }

    public static ServerInfo.Status valueOf(String string) {
        return Enum.valueOf(ServerInfo.Status.class, string);
    }

    private static /* synthetic */ ServerInfo.Status[] method_55826() {
        return new ServerInfo.Status[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
    }

    static {
        field_47885 = ServerInfo.Status.method_55826();
    }
}
