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
static final class ServerResourcePackManager.Status
extends Enum<ServerResourcePackManager.Status> {
    public static final /* enum */ ServerResourcePackManager.Status INACTIVE = new ServerResourcePackManager.Status();
    public static final /* enum */ ServerResourcePackManager.Status PENDING = new ServerResourcePackManager.Status();
    public static final /* enum */ ServerResourcePackManager.Status ACTIVE = new ServerResourcePackManager.Status();
    private static final /* synthetic */ ServerResourcePackManager.Status[] field_47642;

    public static ServerResourcePackManager.Status[] values() {
        return (ServerResourcePackManager.Status[])field_47642.clone();
    }

    public static ServerResourcePackManager.Status valueOf(String string) {
        return Enum.valueOf(ServerResourcePackManager.Status.class, string);
    }

    private static /* synthetic */ ServerResourcePackManager.Status[] method_55572() {
        return new ServerResourcePackManager.Status[]{INACTIVE, PENDING, ACTIVE};
    }

    static {
        field_47642 = ServerResourcePackManager.Status.method_55572();
    }
}
