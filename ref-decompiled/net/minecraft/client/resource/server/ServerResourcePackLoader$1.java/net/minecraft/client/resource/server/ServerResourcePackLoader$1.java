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
class ServerResourcePackLoader.1
implements PackStateChangeCallback {
    ServerResourcePackLoader.1() {
    }

    @Override
    public void onStateChanged(UUID id, PackStateChangeCallback.State state) {
        LOGGER.debug("Downloaded pack {} changed state to {}", (Object)id, (Object)state);
    }

    @Override
    public void onFinish(UUID id, PackStateChangeCallback.FinishState state) {
        LOGGER.debug("Downloaded pack {} finished with state {}", (Object)id, (Object)state);
    }
}
