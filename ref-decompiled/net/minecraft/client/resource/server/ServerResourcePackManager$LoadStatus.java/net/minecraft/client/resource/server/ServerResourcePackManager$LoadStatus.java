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
static final class ServerResourcePackManager.LoadStatus
extends Enum<ServerResourcePackManager.LoadStatus> {
    public static final /* enum */ ServerResourcePackManager.LoadStatus REQUESTED = new ServerResourcePackManager.LoadStatus();
    public static final /* enum */ ServerResourcePackManager.LoadStatus PENDING = new ServerResourcePackManager.LoadStatus();
    public static final /* enum */ ServerResourcePackManager.LoadStatus DONE = new ServerResourcePackManager.LoadStatus();
    private static final /* synthetic */ ServerResourcePackManager.LoadStatus[] field_47646;

    public static ServerResourcePackManager.LoadStatus[] values() {
        return (ServerResourcePackManager.LoadStatus[])field_47646.clone();
    }

    public static ServerResourcePackManager.LoadStatus valueOf(String string) {
        return Enum.valueOf(ServerResourcePackManager.LoadStatus.class, string);
    }

    private static /* synthetic */ ServerResourcePackManager.LoadStatus[] method_55573() {
        return new ServerResourcePackManager.LoadStatus[]{REQUESTED, PENDING, DONE};
    }

    static {
        field_47646 = ServerResourcePackManager.LoadStatus.method_55573();
    }
}
