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

@Environment(value=EnvType.CLIENT)
class ServerResourcePackLoader.2
implements PackStateChangeCallback {
    ServerResourcePackLoader.2() {
    }

    @Override
    public void onStateChanged(UUID id, PackStateChangeCallback.State state) {
        ServerResourcePackLoader.this.packStateChangeCallback.onStateChanged(id, state);
    }

    @Override
    public void onFinish(UUID id, PackStateChangeCallback.FinishState state) {
        ServerResourcePackLoader.this.packStateChangeCallback.onFinish(id, state);
    }
}
