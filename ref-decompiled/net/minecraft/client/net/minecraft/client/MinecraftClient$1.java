/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientWatchdog;
import net.minecraft.client.RunArgs;

@Environment(value=EnvType.CLIENT)
class MinecraftClient.1
implements Runnable {
    private boolean closed;
    final /* synthetic */ RunArgs field_52754;

    MinecraftClient.1() {
        this.field_52754 = runArgs;
    }

    @Override
    public void run() {
        if (!this.closed) {
            this.closed = true;
            ClientWatchdog.shutdownClient(this.field_52754.directories.runDir, MinecraftClient.this.thread.threadId());
        }
    }
}
