/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;

@Environment(value=EnvType.CLIENT)
static class ServerResourcePackLoader.6
implements PackStateChangeCallback {
    final /* synthetic */ ClientConnection field_47693;

    ServerResourcePackLoader.6(ClientConnection clientConnection) {
        this.field_47693 = clientConnection;
    }

    @Override
    public void onStateChanged(UUID id, PackStateChangeCallback.State state) {
        LOGGER.debug("Pack {} changed status to {}", (Object)id, (Object)state);
        ResourcePackStatusC2SPacket.Status status = switch (state) {
            default -> throw new MatchException(null, null);
            case PackStateChangeCallback.State.ACCEPTED -> ResourcePackStatusC2SPacket.Status.ACCEPTED;
            case PackStateChangeCallback.State.DOWNLOADED -> ResourcePackStatusC2SPacket.Status.DOWNLOADED;
        };
        this.field_47693.send(new ResourcePackStatusC2SPacket(id, status));
    }

    @Override
    public void onFinish(UUID id, PackStateChangeCallback.FinishState state) {
        LOGGER.debug("Pack {} changed status to {}", (Object)id, (Object)state);
        ResourcePackStatusC2SPacket.Status status = switch (state) {
            default -> throw new MatchException(null, null);
            case PackStateChangeCallback.FinishState.APPLIED -> ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED;
            case PackStateChangeCallback.FinishState.DOWNLOAD_FAILED -> ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD;
            case PackStateChangeCallback.FinishState.DECLINED -> ResourcePackStatusC2SPacket.Status.DECLINED;
            case PackStateChangeCallback.FinishState.DISCARDED -> ResourcePackStatusC2SPacket.Status.DISCARDED;
            case PackStateChangeCallback.FinishState.ACTIVATION_FAILED -> ResourcePackStatusC2SPacket.Status.FAILED_RELOAD;
        };
        this.field_47693.send(new ResourcePackStatusC2SPacket(id, status));
    }
}
