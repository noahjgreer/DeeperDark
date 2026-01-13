/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;

@Environment(value=EnvType.CLIENT)
class ServerResourcePackLoader.7
implements PackStateChangeCallback {
    final /* synthetic */ PackStateChangeCallback field_47694;
    final /* synthetic */ UUID field_47695;
    final /* synthetic */ CompletableFuture field_47696;

    ServerResourcePackLoader.7() {
        this.field_47694 = packStateChangeCallback;
        this.field_47695 = uUID;
        this.field_47696 = completableFuture;
    }

    @Override
    public void onStateChanged(UUID id, PackStateChangeCallback.State state) {
        this.field_47694.onStateChanged(id, state);
    }

    @Override
    public void onFinish(UUID id, PackStateChangeCallback.FinishState state) {
        if (this.field_47695.equals(id)) {
            ServerResourcePackLoader.this.packStateChangeCallback = this.field_47694;
            if (state == PackStateChangeCallback.FinishState.APPLIED) {
                this.field_47696.complete(null);
            } else {
                this.field_47696.completeExceptionally(new IllegalStateException("Failed to apply pack " + String.valueOf(id) + ", reason: " + String.valueOf((Object)state)));
            }
        }
        this.field_47694.onFinish(id, state);
    }
}
