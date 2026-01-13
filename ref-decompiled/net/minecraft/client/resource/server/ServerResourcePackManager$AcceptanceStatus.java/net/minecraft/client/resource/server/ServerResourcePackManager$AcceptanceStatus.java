/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class ServerResourcePackManager.AcceptanceStatus
extends Enum<ServerResourcePackManager.AcceptanceStatus> {
    public static final /* enum */ ServerResourcePackManager.AcceptanceStatus PENDING = new ServerResourcePackManager.AcceptanceStatus();
    public static final /* enum */ ServerResourcePackManager.AcceptanceStatus ALLOWED = new ServerResourcePackManager.AcceptanceStatus();
    public static final /* enum */ ServerResourcePackManager.AcceptanceStatus DECLINED = new ServerResourcePackManager.AcceptanceStatus();
    private static final /* synthetic */ ServerResourcePackManager.AcceptanceStatus[] field_47650;

    public static ServerResourcePackManager.AcceptanceStatus[] values() {
        return (ServerResourcePackManager.AcceptanceStatus[])field_47650.clone();
    }

    public static ServerResourcePackManager.AcceptanceStatus valueOf(String string) {
        return Enum.valueOf(ServerResourcePackManager.AcceptanceStatus.class, string);
    }

    private static /* synthetic */ ServerResourcePackManager.AcceptanceStatus[] method_55574() {
        return new ServerResourcePackManager.AcceptanceStatus[]{PENDING, ALLOWED, DECLINED};
    }

    static {
        field_47650 = ServerResourcePackManager.AcceptanceStatus.method_55574();
    }
}
