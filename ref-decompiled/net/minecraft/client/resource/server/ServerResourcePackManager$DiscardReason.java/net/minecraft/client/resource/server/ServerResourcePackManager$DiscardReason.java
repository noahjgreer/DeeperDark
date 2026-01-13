/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class ServerResourcePackManager.DiscardReason
extends Enum<ServerResourcePackManager.DiscardReason> {
    public static final /* enum */ ServerResourcePackManager.DiscardReason DOWNLOAD_FAILED = new ServerResourcePackManager.DiscardReason(PackStateChangeCallback.FinishState.DOWNLOAD_FAILED);
    public static final /* enum */ ServerResourcePackManager.DiscardReason ACTIVATION_FAILED = new ServerResourcePackManager.DiscardReason(PackStateChangeCallback.FinishState.ACTIVATION_FAILED);
    public static final /* enum */ ServerResourcePackManager.DiscardReason DECLINED = new ServerResourcePackManager.DiscardReason(PackStateChangeCallback.FinishState.DECLINED);
    public static final /* enum */ ServerResourcePackManager.DiscardReason DISCARDED = new ServerResourcePackManager.DiscardReason(PackStateChangeCallback.FinishState.DISCARDED);
    public static final /* enum */ ServerResourcePackManager.DiscardReason SERVER_REMOVED = new ServerResourcePackManager.DiscardReason(null);
    public static final /* enum */ ServerResourcePackManager.DiscardReason SERVER_REPLACED = new ServerResourcePackManager.DiscardReason(null);
    final  @Nullable PackStateChangeCallback.FinishState state;
    private static final /* synthetic */ ServerResourcePackManager.DiscardReason[] field_47658;

    public static ServerResourcePackManager.DiscardReason[] values() {
        return (ServerResourcePackManager.DiscardReason[])field_47658.clone();
    }

    public static ServerResourcePackManager.DiscardReason valueOf(String string) {
        return Enum.valueOf(ServerResourcePackManager.DiscardReason.class, string);
    }

    private ServerResourcePackManager.DiscardReason(PackStateChangeCallback.FinishState state) {
        this.state = state;
    }

    private static /* synthetic */ ServerResourcePackManager.DiscardReason[] method_55575() {
        return new ServerResourcePackManager.DiscardReason[]{DOWNLOAD_FAILED, ACTIVATION_FAILED, DECLINED, DISCARDED, SERVER_REMOVED, SERVER_REPLACED};
    }

    static {
        field_47658 = ServerResourcePackManager.DiscardReason.method_55575();
    }
}
