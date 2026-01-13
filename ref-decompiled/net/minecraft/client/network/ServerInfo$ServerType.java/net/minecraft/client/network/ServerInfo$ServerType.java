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
public static final class ServerInfo.ServerType
extends Enum<ServerInfo.ServerType> {
    public static final /* enum */ ServerInfo.ServerType LAN = new ServerInfo.ServerType();
    public static final /* enum */ ServerInfo.ServerType REALM = new ServerInfo.ServerType();
    public static final /* enum */ ServerInfo.ServerType OTHER = new ServerInfo.ServerType();
    private static final /* synthetic */ ServerInfo.ServerType[] field_45612;

    public static ServerInfo.ServerType[] values() {
        return (ServerInfo.ServerType[])field_45612.clone();
    }

    public static ServerInfo.ServerType valueOf(String string) {
        return Enum.valueOf(ServerInfo.ServerType.class, string);
    }

    private static /* synthetic */ ServerInfo.ServerType[] method_52812() {
        return new ServerInfo.ServerType[]{LAN, REALM, OTHER};
    }

    static {
        field_45612 = ServerInfo.ServerType.method_52812();
    }
}
