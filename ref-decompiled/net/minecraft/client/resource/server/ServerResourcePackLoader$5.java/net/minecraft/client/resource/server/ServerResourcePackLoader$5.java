/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
class ServerResourcePackLoader.5
implements Runnable {
    private boolean currentlyRunning;
    private boolean shouldKeepRunning;
    final /* synthetic */ Executor field_47616;

    ServerResourcePackLoader.5() {
        this.field_47616 = executor;
    }

    @Override
    public void run() {
        this.shouldKeepRunning = true;
        if (!this.currentlyRunning) {
            this.currentlyRunning = true;
            this.field_47616.execute(this::runOnExecutor);
        }
    }

    private void runOnExecutor() {
        while (this.shouldKeepRunning) {
            this.shouldKeepRunning = false;
            ServerResourcePackLoader.this.manager.update();
        }
        this.currentlyRunning = false;
    }
}
